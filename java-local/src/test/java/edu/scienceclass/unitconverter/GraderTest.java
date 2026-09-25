package edu.scienceclass.unitconverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraderTest {

    @Test
    void fahrenheitToRankine_correctStudentAnswer() {
        Grader.GradeOutcome outcome = Grader.grade(84.2, Unit.FAHRENHEIT, Unit.RANKINE, "543.94");
        assertEquals(Grader.Result.CORRECT, outcome.result);
    }

    @Test
    void kelvinToFahrenheit_incorrectStudentAnswer() {
        Grader.GradeOutcome outcome = Grader.grade(317.33, Unit.KELVIN, Unit.FAHRENHEIT, "111.554");
        assertEquals(Grader.Result.INCORRECT, outcome.result);
    }

    @Test
    void cupsToLiters_correctStudentAnswer() {
        Grader.GradeOutcome outcome = Grader.grade(25.6, Unit.CUP, Unit.LITER, "6.1");
        assertEquals(Grader.Result.CORRECT, outcome.result);
    }

    @Test
    void nonNumericStudentResponse_isInvalid() {
        Grader.GradeOutcome outcome = Grader.grade(6.5, Unit.FAHRENHEIT, Unit.RANKINE, "dogcow");
        assertEquals(Grader.Result.INVALID, outcome.result);
    }

    @Test
    void emptyStudentResponse_isInvalid() {
        Grader.GradeOutcome outcome = Grader.grade(6.5, Unit.FAHRENHEIT, Unit.RANKINE, "");
        assertEquals(Grader.Result.INVALID, outcome.result);
    }

    @Test
    void whitespaceOnlyStudentResponse_isInvalid() {
        Grader.GradeOutcome outcome = Grader.grade(6.5, Unit.FAHRENHEIT, Unit.RANKINE, "   ");
        assertEquals(Grader.Result.INVALID, outcome.result);
    }

    @Test
    void isNumeric_acceptsAndRejectsExpectedValues() {
        assertTrue(Grader.isNumeric("42.5"));
        assertTrue(Grader.isNumeric("-40.0"));
        assertFalse(Grader.isNumeric("dogcow"));
        assertFalse(Grader.isNumeric(""));
        assertFalse(Grader.isNumeric(null));
    }
}
