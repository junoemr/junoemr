UPDATE validations
  SET maxValue1 = 10000,
  isDate = NULL,
  minLength = NULL,
  maxLength = NULL
WHERE id = (SELECT id from validations WHERE name = 'Numeric Value greater than or equal to 0');

UPDATE measurementType
  SET validation = (SELECT id FROM validations WHERE name = 'Numeric Value greater than or equal to 0')
WHERE type = 'WT';
