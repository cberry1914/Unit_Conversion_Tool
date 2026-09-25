/**
 * converter.js
 * ------------
 * Pure conversion + grading logic for the Science Class Unit Converter.
 *
 * This file has NO dependency on the DOM, so it can be:
 *   1. Loaded directly in the browser via a <script> tag (app.js wires it to the UI), and
 *   2. Required from Node.js for automated tests (see web-js/tests/converter.test.js).
 *
 * Design notes:
 *   - All temperature conversions route through Kelvin as a canonical intermediate unit.
 *   - All volume conversions route through Liters as a canonical intermediate unit.
 *   - Volume unit definitions use US customary measures (US cup, US tablespoon, US gallon).
 */

(function (global) {
  'use strict';

  const UNIT_CATEGORY = Object.freeze({
    KELVIN: 'TEMPERATURE',
    CELSIUS: 'TEMPERATURE',
    FAHRENHEIT: 'TEMPERATURE',
    RANKINE: 'TEMPERATURE',
    LITER: 'VOLUME',
    TABLESPOON: 'VOLUME',
    CUBIC_INCH: 'VOLUME',
    CUP: 'VOLUME',
    CUBIC_FOOT: 'VOLUME',
    GALLON: 'VOLUME',
  });

  const UNIT_LABELS = Object.freeze({
    KELVIN: 'Kelvin',
    CELSIUS: 'Celsius',
    FAHRENHEIT: 'Fahrenheit',
    RANKINE: 'Rankine',
    LITER: 'Liters',
    TABLESPOON: 'Tablespoons',
    CUBIC_INCH: 'Cubic Inches',
    CUP: 'Cups',
    CUBIC_FOOT: 'Cubic Feet',
    GALLON: 'Gallons',
  });

  // Liters-per-unit conversion factors (US customary definitions).
  const LITERS_PER_UNIT = Object.freeze({
    LITER: 1,
    TABLESPOON: 0.0147867648,
    CUBIC_INCH: 0.016387064,
    CUP: 0.2365882365,
    CUBIC_FOOT: 28.316846592,
    GALLON: 3.785411784,
  });

  function toKelvin(value, unit) {
    switch (unit) {
      case 'KELVIN':
        return value;
      case 'CELSIUS':
        return value + 273.15;
      case 'FAHRENHEIT':
        return (value - 32) * (5 / 9) + 273.15;
      case 'RANKINE':
        return value * (5 / 9);
      default:
        throw new Error(`Unknown temperature unit: ${unit}`);
    }
  }

  function fromKelvin(kelvin, unit) {
    switch (unit) {
      case 'KELVIN':
        return kelvin;
      case 'CELSIUS':
        return kelvin - 273.15;
      case 'FAHRENHEIT':
        return (kelvin - 273.15) * (9 / 5) + 32;
      case 'RANKINE':
        return kelvin * (9 / 5);
      default:
        throw new Error(`Unknown temperature unit: ${unit}`);
    }
  }

  /**
   * Converts `value` from `fromUnit` to `toUnit`.
   * Throws if the units are unrecognized or belong to different categories
   * (this should be structurally impossible from the UI, since the target
   * dropdown is always filtered to the same category as the input dropdown,
   * but the guard stays here as defense-in-depth for any non-UI caller,
   * e.g. automated tests or a future API layer).
   */
  function convert(value, fromUnit, toUnit) {
    const fromCategory = UNIT_CATEGORY[fromUnit];
    const toCategory = UNIT_CATEGORY[toUnit];
    if (!fromCategory || !toCategory) {
      throw new Error(`Unrecognized unit(s): ${fromUnit}, ${toUnit}`);
    }
    if (fromCategory !== toCategory) {
      throw new Error(`Cannot convert across categories: ${fromCategory} -> ${toCategory}`);
    }
    if (fromCategory === 'TEMPERATURE') {
      return fromKelvin(toKelvin(value, fromUnit), toUnit);
    }
    const liters = value * LITERS_PER_UNIT[fromUnit];
    return liters / LITERS_PER_UNIT[toUnit];
  }

  /** Rounds to one decimal place ("the tenths place"). */
  function roundToTenth(value) {
    return Math.round((value + Number.EPSILON) * 10) / 10;
  }

  /** True if `raw` (a string from a text input) parses as a finite number. */
  function isNumeric(raw) {
    if (typeof raw !== 'string') return false;
    const trimmed = raw.trim();
    if (trimmed === '') return false;
    return Number.isFinite(Number(trimmed));
  }

  /**
   * Grades a student's response.
   *
   * Rules (confirmed with the instructor building this tool):
   *   - INVALID  -> the student's response is not a numeric value.
   *   - CORRECT  -> the student's response, rounded to the tenths place,
   *                 equals the authoritative converted value, also rounded
   *                 to the tenths place.
   *   - INCORRECT -> the student's response is numeric but does not match.
   *
   * Category mismatches (e.g. Gallons -> Kelvin) are prevented at the UI
   * layer by filtering the target dropdown, so they are not graded here;
   * `convert()` will throw if one is attempted directly.
   *
   * @returns {{result: 'Correct'|'Incorrect'|'Invalid', authoritative: number|null}}
   */
  function gradeResponse(inputValue, inputUnit, targetUnit, studentResponseRaw) {
    if (!isNumeric(studentResponseRaw)) {
      return { result: 'Invalid', authoritative: null };
    }
    const authoritative = convert(Number(inputValue), inputUnit, targetUnit);
    const roundedAuthoritative = roundToTenth(authoritative);
    const roundedStudent = roundToTenth(Number(studentResponseRaw));
    const result = roundedAuthoritative === roundedStudent ? 'Correct' : 'Incorrect';
    return { result, authoritative: roundedAuthoritative };
  }

  const api = {
    UNIT_CATEGORY,
    UNIT_LABELS,
    LITERS_PER_UNIT,
    toKelvin,
    fromKelvin,
    convert,
    roundToTenth,
    isNumeric,
    gradeResponse,
  };

  if (typeof module !== 'undefined' && module.exports) {
    module.exports = api;
  } else {
    global.UnitConverter = api;
  }
})(typeof window !== 'undefined' ? window : globalThis);
