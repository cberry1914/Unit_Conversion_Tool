package edu.scienceclass.unitconverter;

/**
 * Grades a student's response against the authoritative converted value.
 *
 * Rules:
 *   - INVALID   -> the student's response is not a numeric value.
 *   - CORRECT   -> the student's response, rounded to the tenths place,
 *                  equals the authoritative converted value, also rounded
 *                  to the tenths place.
 *   - INCORRECT -> the student's response is numeric but does not match.
 */
public final class Grader {

    public enum Result {
        CORRECT,
        INCORRECT,
        INVALID
    }

    public static final class GradeOutcome {
        public final Result result;
        public final Double authoritativeValue; // null when result == INVALID

        public GradeOutcome(Result result, Double authoritativeValue) {
            this.result = result;
            this.authoritativeValue = authoritativeValue;
        }
    }

    private Grader() {
        // static utility class
    }

    /** True if {@code raw} parses as a finite number once trimmed. */
    public static boolean isNumeric(String raw) {
        if (raw == null) {
            return false;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        try {
            double parsed = Double.parseDouble(trimmed);
            return !Double.isNaN(parsed) && !Double.isInfinite(parsed);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static GradeOutcome grade(double inputValue, Unit inputUnit, Unit targetUnit, String studentResponseRaw) {
        if (!isNumeric(studentResponseRaw)) {
            return new GradeOutcome(Result.INVALID, null);
        }
        double authoritative = ConversionEngine.convert(inputValue, inputUnit, targetUnit);
        double roundedAuthoritative = ConversionEngine.roundToTenth(authoritative);
        double roundedStudent = ConversionEngine.roundToTenth(Double.parseDouble(studentResponseRaw.trim()));
        Result result = (roundedAuthoritative == roundedStudent) ? Result.CORRECT : Result.INCORRECT;
        return new GradeOutcome(result, roundedAuthoritative);
    }
}
