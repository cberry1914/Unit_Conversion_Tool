const { convert, gradeResponse, roundToTenth, isNumeric } = require('../js/converter');

describe('temperature conversions', () => {
  test('Fahrenheit to Rankine (84.2F)', () => {
    expect(roundToTenth(convert(84.2, 'FAHRENHEIT', 'RANKINE'))).toBeCloseTo(543.9, 1);
  });

  test('Kelvin to Fahrenheit (317.33K)', () => {
    expect(roundToTenth(convert(317.33, 'KELVIN', 'FAHRENHEIT'))).toBeCloseTo(111.5, 1);
  });

  test('round trip Celsius -> Kelvin -> Celsius is stable', () => {
    const k = convert(20, 'CELSIUS', 'KELVIN');
    const back = convert(k, 'KELVIN', 'CELSIUS');
    expect(back).toBeCloseTo(20, 9);
  });
});

describe('volume conversions', () => {
  test('cups to liters (25.6 cups)', () => {
    expect(roundToTenth(convert(25.6, 'CUP', 'LITER'))).toBeCloseTo(6.1, 1);
  });

  test('gallons to liters (1 gallon)', () => {
    expect(convert(1, 'GALLON', 'LITER')).toBeCloseTo(3.785411784, 6);
  });

  test('cubic feet to gallons round trip', () => {
    const gal = convert(1, 'CUBIC_FOOT', 'GALLON');
    const back = convert(gal, 'GALLON', 'CUBIC_FOOT');
    expect(back).toBeCloseTo(1, 9);
  });
});

describe('cross-category conversion is rejected', () => {
  test('gallons to Kelvin throws', () => {
    expect(() => convert(73.12, 'GALLON', 'KELVIN')).toThrow();
  });
});

describe('grading logic (matches the instructor-provided example table)', () => {
  test('84.2F -> Rankine, student "543.94" => Correct', () => {
    expect(gradeResponse(84.2, 'FAHRENHEIT', 'RANKINE', '543.94').result).toBe('Correct');
  });

  test('317.33K -> Fahrenheit, student "111.554" => Incorrect', () => {
    expect(gradeResponse(317.33, 'KELVIN', 'FAHRENHEIT', '111.554').result).toBe('Incorrect');
  });

  test('25.6 cups -> liters, student "6.1" => Correct', () => {
    expect(gradeResponse(25.6, 'CUP', 'LITER', '6.1').result).toBe('Correct');
  });

  test('6.5F -> Rankine, student "dogcow" => Invalid (non-numeric response)', () => {
    expect(gradeResponse(6.5, 'FAHRENHEIT', 'RANKINE', 'dogcow').result).toBe('Invalid');
  });

  test('empty student response => Invalid', () => {
    expect(gradeResponse(6.5, 'FAHRENHEIT', 'RANKINE', '').result).toBe('Invalid');
  });

  test('whitespace-only student response => Invalid', () => {
    expect(gradeResponse(6.5, 'FAHRENHEIT', 'RANKINE', '   ').result).toBe('Invalid');
  });
});

describe('isNumeric', () => {
  test('rejects empty string', () => expect(isNumeric('')).toBe(false));
  test('rejects non-numeric text', () => expect(isNumeric('dogcow')).toBe(false));
  test('accepts a plain integer', () => expect(isNumeric('42')).toBe(true));
  test('accepts a decimal', () => expect(isNumeric('42.5')).toBe(true));
  test('accepts a negative decimal', () => expect(isNumeric('-40.0')).toBe(true));
});

describe('roundToTenth', () => {
  test('rounds up correctly', () => expect(roundToTenth(6.056659)).toBe(6.1));
  test('rounds down correctly', () => expect(roundToTenth(111.524)).toBe(111.5));
});
