package edu.scienceclass.unitconverter;

import java.util.EnumMap;
import java.util.Map;

/**
 * Pure conversion math — no Swing/UI dependency, so it can be unit tested
 * headlessly (see {@code src/test/java/.../ConversionEngineTest.java}), which
 * matters because CI runners have no display for a Swing GUI to attach to.
 *
 * Temperature conversions route through Kelvin as a canonical intermediate
 * unit. Volume conversions route through Liters. Volume unit definitions use
 * US customary measures (US cup, US tablespoon, US gallon) to match
 * web-js/js/converter.js exactly.
 */
public final class ConversionEngine {

    private static final Map<Unit, Double> LITERS_PER_UNIT = new EnumMap<>(Unit.class);

    static {
        LITERS_PER_UNIT.put(Unit.LITER, 1.0);
        LITERS_PER_UNIT.put(Unit.TABLESPOON, 0.0147867648);
        LITERS_PER_UNIT.put(Unit.CUBIC_INCH, 0.016387064);
        LITERS_PER_UNIT.put(Unit.CUP, 0.2365882365);
        LITERS_PER_UNIT.put(Unit.CUBIC_FOOT, 28.316846592);
        LITERS_PER_UNIT.put(Unit.GALLON, 3.785411784);
    }

    private ConversionEngine() {
        // static utility class
    }

    public static double toKelvin(double value, Unit unit) {
        switch (unit) {
            case KELVIN:
                return value;
            case CELSIUS:
                return value + 273.15;
            case FAHRENHEIT:
                return (value - 32) * (5.0 / 9.0) + 273.15;
            case RANKINE:
                return value * (5.0 / 9.0);
            default:
                throw new IllegalArgumentException("Not a temperature unit: " + unit);
        }
    }

    public static double fromKelvin(double kelvin, Unit unit) {
        switch (unit) {
            case KELVIN:
                return kelvin;
            case CELSIUS:
                return kelvin - 273.15;
            case FAHRENHEIT:
                return (kelvin - 273.15) * (9.0 / 5.0) + 32;
            case RANKINE:
                return kelvin * (9.0 / 5.0);
            default:
                throw new IllegalArgumentException("Not a temperature unit: " + unit);
        }
    }

    /**
     * Converts {@code value} from {@code from} to {@code to}. Throws if the
     * two units belong to different categories. The Swing GUI structurally
     * prevents this by filtering the target dropdown, but this guard remains
     * as defense-in-depth for any other caller (tests, a future CLI, etc.).
     */
    public static double convert(double value, Unit from, Unit to) {
        if (from.getCategory() != to.getCategory()) {
            throw new IllegalArgumentException(
                    "Cannot convert across categories: " + from.getCategory() + " -> " + to.getCategory());
        }
        if (from.getCategory() == Unit.Category.TEMPERATURE) {
            return fromKelvin(toKelvin(value, from), to);
        }
        double liters = value * LITERS_PER_UNIT.get(from);
        return liters / LITERS_PER_UNIT.get(to);
    }

    /** Rounds to one decimal place ("the tenths place"). */
    public static double roundToTenth(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
