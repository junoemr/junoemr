INSERT INTO `measurementType` (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
    SELECT 'GAD7', 'GAD7', 'GAD7', '', 14, NOW()
        FROM dual
        WHERE NOT EXISTS (SELECT type FROM `measurementType`
                            WHERE type='GAD7');

INSERT INTO `measurementType` (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
    SELECT 'PHQ9', 'PHQ9', 'PHQ9', '', 14, NOW()
        FROM dual
        WHERE NOT EXISTS (SELECT type FROM `measurementType`
                            WHERE type='PHQ9');


