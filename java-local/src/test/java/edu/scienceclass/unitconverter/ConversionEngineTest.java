package edu.scienceclass.unitconverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversionEngineTest {

    private static final double DELTA = 1e-6;

    @Test
    void fahrenheitToRankine_matchesExample() {
        double result = ConversionEngine.convert(84.2, Unit.FAHRENHEIT, Unit.RANKINE);
        assertEquals(543.9, ConversionEngine.roundToTenth(result), DELTA);
    }

    @Test
    void kelvinToFahrenheit_matchesExample() {
        double result = ConversionEngine.convert(317.33, Unit.KELVIN, Unit.FAHRENHEIT);
        assertEquals(111.5, ConversionEngine.roundToTenth(result), DELTA);
    }

    @Test
    void celsiusToKelvinRoundTrip_isStable() {
        double k = ConversionEngine.convert(20, Unit.CELSIUS, Unit.KELVIN);
        double back = ConversionEngine.convert(k, Unit.KELVIN, Unit.CELSIUS);
        assertEquals(20.0, back, DELTA);
    }

    @Test
    void cupsToLiters_matchesExample() {
        double result = ConversionEngine.convert(25.6, Unit.CUP, Unit.LITER);
        assertEquals(6.1, ConversionEngine.roundToTenth(result), DELTA);
    }

    @Test
    void gallonToLiter_matchesKnownFactor() {
        double result = ConversionEngine.convert(1, Unit.GALLON, Unit.LITER);
        assertEquals(3.785411784, result, 1e-9);
    }

    @Test
    void cubicFootToGallonRoundTrip_isStable() {
        double gal = ConversionEngine.convert(1, Unit.CUBIC_FOOT, Unit.GALLON);
        double back = ConversionEngine.convert(gal, Unit.GALLON, Unit.CUBIC_FOOT);
        assertEquals(1.0, back, 1e-9);
    }

    @Test
    void crossCategoryConversion_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> ConversionEngine.convert(73.12, Unit.GALLON, Unit.KELVIN));
    }
}
