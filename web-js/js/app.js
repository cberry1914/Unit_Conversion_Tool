/**
 * app.js
 * ------
 * Wires the pure logic in converter.js (window.UnitConverter) to the DOM.
 * Keeping this file separate from converter.js is a deliberate architecture
 * choice: converter.js has zero DOM dependencies and is fully unit-testable
 * under Node/Jest, while app.js is the (thin, mostly untested) glue layer.
 */

(function () {
  'use strict';

  const { UNIT_CATEGORY, UNIT_LABELS, gradeResponse, isNumeric } = window.UnitConverter;

  const inputValueEl = document.getElementById('inputValue');
  const inputUnitEl = document.getElementById('inputUnit');
  const targetUnitEl = document.getElementById('targetUnit');
  const studentResponseEl = document.getElementById('studentResponse');
  const checkButtonEl = document.getElementById('checkButton');
  const resultEl = document.getElementById('result');
  const expectedHintEl = document.getElementById('expectedHint');

  const TEMPERATURE_UNITS = ['KELVIN', 'CELSIUS', 'FAHRENHEIT', 'RANKINE'];
  const VOLUME_UNITS = ['LITER', 'TABLESPOON', 'CUBIC_INCH', 'CUP', 'CUBIC_FOOT', 'GALLON'];

  function populateInputUnitDropdown() {
    inputUnitEl.innerHTML = '';

    const tempGroup = document.createElement('optgroup');
    tempGroup.label = 'Temperature';
    TEMPERATURE_UNITS.forEach((unit) => tempGroup.appendChild(makeOption(unit)));

    const volGroup = document.createElement('optgroup');
    volGroup.label = 'Volume';
    VOLUME_UNITS.forEach((unit) => volGroup.appendChild(makeOption(unit)));

    inputUnitEl.appendChild(tempGroup);
    inputUnitEl.appendChild(volGroup);
  }

  function makeOption(unitKey) {
    const opt = document.createElement('option');
    opt.value = unitKey;
    opt.textContent = UNIT_LABELS[unitKey];
    return opt;
  }

  /**
   * Refreshes the target-unit dropdown so it only offers units in the same
   * category as the currently selected input unit (and excludes that exact
   * unit, since "convert X to itself" isn't a meaningful classroom question).
   * This is what structurally prevents a category-mismatch (e.g. Gallons ->
   * Kelvin) from ever reaching the grading logic.
   */
  function refreshTargetUnitDropdown() {
    const selected = inputUnitEl.value;
    const category = UNIT_CATEGORY[selected];
    const sameCategoryUnits = (category === 'TEMPERATURE' ? TEMPERATURE_UNITS : VOLUME_UNITS).filter(
      (u) => u !== selected
    );

    targetUnitEl.innerHTML = '';
    sameCategoryUnits.forEach((unit) => targetUnitEl.appendChild(makeOption(unit)));
  }

  function setResult(text, cssClass) {
    resultEl.textContent = text;
    resultEl.className = 'result ' + cssClass;
  }

  function handleCheck() {
    expectedHintEl.textContent = '';

    const rawInputValue = inputValueEl.value;
    if (!isNumeric(rawInputValue)) {
      setResult('Please enter a valid numeric input value.', 'result--warning');
      return;
    }

    const inputValue = Number(rawInputValue);
    const inputUnit = inputUnitEl.value;
    const targetUnit = targetUnitEl.value;
    const studentResponse = studentResponseEl.value;

    const outcome = gradeResponse(inputValue, inputUnit, targetUnit, studentResponse);

    if (outcome.result === 'Correct') {
      setResult('Correct', 'result--correct');
    } else if (outcome.result === 'Incorrect') {
      setResult('Incorrect', 'result--incorrect');
    } else {
      setResult('Invalid — student response must be a numeric value.', 'result--invalid');
    }

    if (outcome.authoritative !== null) {
      expectedHintEl.textContent = `Correct answer: ${outcome.authoritative} ${UNIT_LABELS[targetUnit]}`;
    }
  }

  function init() {
    populateInputUnitDropdown();
    refreshTargetUnitDropdown();
    inputUnitEl.addEventListener('change', refreshTargetUnitDropdown);
    checkButtonEl.addEventListener('click', handleCheck);
  }

  document.addEventListener('DOMContentLoaded', init);
})();
