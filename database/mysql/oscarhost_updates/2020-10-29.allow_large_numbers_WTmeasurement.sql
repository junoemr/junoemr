UPDATE validations
  SET maxValue1 = 10000,
  isDate = NULL,
  minLength = NULL,
  maxLength = NULL
WHERE id = 14 AND name = 'Numeric Value greater than or equal to 0';

UPDATE measurementType
  SET validation = '14'
WHERE type = 'WT';
