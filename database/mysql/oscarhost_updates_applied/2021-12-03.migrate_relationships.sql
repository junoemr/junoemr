INSERT INTO DemographicContact (facilityId, creator, created, updateDate, deleted, demographicNo,
                                contactId, role, type, category, sdm, ec, note, consentToContact,
                                active, mrp)
SELECT facility_id                                 AS facilityId,
       creator                                     AS creator,
       creation_date                               AS created,
       creation_date                               AS updateDate,
       IF(deleted = 1, 1, 0)                       AS deleted,          -- Relationships table uses NULL as not deleted instead of 0
       demographic_no                              AS demographicNo,
       relation_demographic_no                     AS contactId,
       relation                                    AS role,
       1                                           AS type,             -- Internal Contact
       'personal'                                  AS category,
       IF(sub_decision_maker = 1, 'true', 'false') AS sdm,
       IF(emergency_contact = 1, 'true', 'false')  AS ec,
       notes                                       AS note,
       0                                           AS consentToContact, -- Default to no consent given as this is not captured in relationships
       1                                           AS active,
       NULL                                        AS mrp
FROM relationships
WHERE (demographic_no, relation_demographic_no, relation, 1) NOT IN
      (
          SELECT demographic_no, contactId, role, type
          FROM DemographicContact
      )
;