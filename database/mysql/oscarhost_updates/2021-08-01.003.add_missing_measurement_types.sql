
INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
  SELECT 'POSK', 'Smoking Packs', 'packs of cigarettes smoked per day', '# of packs', v.id, NOW()
  FROM validations v
  WHERE v.name = 'Numeric Value greater than or equal to 0'
  AND v.id NOT IN (
      SELECT v.id
      FROM measurementType mt JOIN validations v ON (mt.validation = v.id)
      WHERE mt.type = 'POSK'
         AND v.name = 'Numeric Value greater than or equal to 0'
      );

