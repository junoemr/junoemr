# conditional insert. only add if a measurement of type "MMSE" does not already exist
INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate) SELECT "MMSE", "MMSE", "MMSE", "Numeric Value: 0 to 100", 4, NOW() WHERE NOT EXISTS (SELECT type from measurementType WHERE type="MMSE");
