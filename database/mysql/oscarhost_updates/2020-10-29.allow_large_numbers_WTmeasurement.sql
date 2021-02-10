UPDATE measurementType
SET validation =
    (SELECT id
     FROM validations
     WHERE name = 'Numeric Value greater than or equal to 0'
     ORDER BY id
     LIMIT 1)
WHERE validation in
    (SELECT id
     FROM validations
     WHERE name = 'Numeric Value greater than or equal to 0');

DELETE FROM validations WHERE id in (
    WITH validations_to_keep AS (
        SELECT id
        FROM validations
        WHERE name = 'Numeric Value greater than or equal to 0'
        ORDER BY id
        LIMIT 1)
    SELECT v.id
    FROM validations v
    JOIN validations_to_keep
    WHERE v.id != validations_to_keep.id
    AND name = 'Numeric Value greater than or equal to 0');

UPDATE validations
  SET maxValue1 = 10000,
  isDate = NULL,
  minLength = NULL,
  maxLength = NULL
WHERE name = 'Numeric Value greater than or equal to 0';

UPDATE measurementType
  SET validation = (
      SELECT id
      FROM validations
      WHERE name = 'Numeric Value greater than or equal to 0')
WHERE type = 'WT';
