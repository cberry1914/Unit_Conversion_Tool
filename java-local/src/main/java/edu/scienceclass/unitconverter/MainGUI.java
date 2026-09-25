package edu.scienceclass.unitconverter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Desktop GUI entry point for local deployment. Mirrors the fields and
 * behavior of the web app: a numeric input value, an input-unit dropdown, a
 * target-unit dropdown that's filtered to the same category as the input
 * unit, a student-response field, and a Correct / Incorrect / Invalid result.
 *
 * This class is intentionally the ONLY class with a Swing/AWT dependency —
 * all grading logic lives in {@link ConversionEngine} and {@link Grader},
 * which is what lets CI run the test suite on a headless runner.
 */
public class MainGUI extends JFrame {

    private final JTextField inputValueField = new JTextField(10);
    private final JComboBox<Unit> inputUnitCombo = new JComboBox<>(Unit.values());
    private final JComboBox<Unit> targetUnitCombo = new JComboBox<>();
    private final JTextField studentResponseField = new JTextField(10);
    private final JLabel resultLabel = new JLabel(" ");
    private final JButton checkButton = new JButton("Check Answer");

    public MainGUI() {
        super("Unit Conversion Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildUI();
        refreshTargetOptions();
        inputUnitCombo.addActionListener(e -> refreshTargetOptions());
        checkButton.addActionListener(e -> onCheck());
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel instructionsLabel = new JLabel(
        "<html>Enter a value and its unit, choose the target unit, then enter the "
        + "student's response to check whether it's correct.</html>");
        instructionsLabel.setFont(instructionsLabel.getFont().deriveFont(Font.PLAIN, 12f));
        instructionsLabel.setForeground(Color.DARK_GRAY);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.insets = new Insets(0, 6, 14, 6);
        form.add(instructionsLabel, c);
        c.gridwidth = 1;
        c.insets = new Insets(6, 6, 6, 6);
        addRow(form, c, row++, "Input numerical value:", inputValueField);
        addRow(form, c, row++, "Input unit of measure:", inputUnitCombo);
        addRow(form, c, row++, "Target unit of measure:", targetUnitCombo);
        addRow(form, c, row++, "Student response:", studentResponseField);

        c.gridx = 1;
        c.gridy = row++;
        c.gridwidth = 1;
        form.add(checkButton, c);

        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 16f));
        c.gridx = 1;
        c.gridy = row++;
        form.add(resultLabel, c);

        add(form);
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String labelText, JComponent field) {
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        form.add(new JLabel(labelText), c);
        c.gridx = 1;
        c.gridy = row;
        form.add(field, c);
    }

    /**
     * Repopulates the target-unit dropdown with only the units that share the
     * selected input unit's category (excluding the input unit itself). This
     * is what structurally prevents a category mismatch (e.g. Gallons ->
     * Kelvin) from ever being submitted for grading.
     */
    private void refreshTargetOptions() {
        Unit selected = (Unit) inputUnitCombo.getSelectedItem();
        targetUnitCombo.removeAllItems();
        if (selected == null) {
            return;
        }
        List<Unit> sameCategory = new ArrayList<>();
        for (Unit u : Unit.values()) {
            if (u.getCategory() == selected.getCategory() && u != selected) {
                sameCategory.add(u);
            }
        }
        for (Unit u : sameCategory) {
            targetUnitCombo.addItem(u);
        }
    }

    private void onCheck() {
        String rawInputValue = inputValueField.getText();
        if (!Grader.isNumeric(rawInputValue)) {
            resultLabel.setForeground(new Color(138, 90, 18));
            resultLabel.setText("Please enter a valid numeric input value.");
            return;
        }

        double inputValue = Double.parseDouble(rawInputValue.trim());
        Unit inputUnit = (Unit) inputUnitCombo.getSelectedItem();
        Unit targetUnit = (Unit) targetUnitCombo.getSelectedItem();
        String studentResponse = studentResponseField.getText();

        Grader.GradeOutcome outcome = Grader.grade(inputValue, inputUnit, targetUnit, studentResponse);
        switch (outcome.result) {
            case CORRECT:
                resultLabel.setForeground(new Color(0, 128, 0));
                resultLabel.setText("Correct (expected \u2248 " + outcome.authoritativeValue
                        + " " + targetUnit.getLabel() + ")");
                break;
            case INCORRECT:
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Incorrect (expected \u2248 " + outcome.authoritativeValue
                        + " " + targetUnit.getLabel() + ")");
                break;
            case INVALID:
            default:
                resultLabel.setForeground(Color.GRAY);
                resultLabel.setText("Invalid \u2014 student response must be a numeric value.");
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
