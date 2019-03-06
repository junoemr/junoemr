INSERT INTO `measurementType` (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
    SELECT 'GAD7', 'GAD7', 'GAD7', '',
    (SELECT id FROM validations WHERE name='Numeric Value greater than or equal to 0' ORDER BY id ASC LIMIT 1) AS validation,
     NOW()
        FROM dual
        WHERE NOT EXISTS (SELECT type FROM `measurementType`
                            WHERE type='GAD7');

INSERT INTO `measurementType` (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
    SELECT 'PHQ9', 'PHQ9', 'PHQ9', '',
    (SELECT id FROM validations WHERE name='Numeric Value greater than or equal to 0' ORDER BY id ASC LIMIT 1) AS validation,
     NOW()
        FROM dual
        WHERE NOT EXISTS (SELECT type FROM `measurementType`
                            WHERE type='PHQ9');


