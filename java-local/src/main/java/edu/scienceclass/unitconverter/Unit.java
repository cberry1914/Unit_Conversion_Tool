package edu.scienceclass.unitconverter;

/**
 * The ten supported units, each tagged with the {@link Category} it belongs
 * to. The category is what the GUI uses to filter the "target unit" dropdown
 * so a student is never asked to convert, say, gallons into Kelvin.
 */
public enum Unit {
    KELVIN("Kelvin", Category.TEMPERATURE),
    CELSIUS("Celsius", Category.TEMPERATURE),
    FAHRENHEIT("Fahrenheit", Category.TEMPERATURE),
    RANKINE("Rankine", Category.TEMPERATURE),

    LITER("Liters", Category.VOLUME),
    TABLESPOON("Tablespoons", Category.VOLUME),
    CUBIC_INCH("Cubic Inches", Category.VOLUME),
    CUP("Cups", Category.VOLUME),
    CUBIC_FOOT("Cubic Feet", Category.VOLUME),
    GALLON("Gallons", Category.VOLUME);

    public enum Category {
        TEMPERATURE,
        VOLUME
    }

    private final String label;
    private final Category category;

    Unit(String label, Category category) {
        this.label = label;
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return label;
    }
}
