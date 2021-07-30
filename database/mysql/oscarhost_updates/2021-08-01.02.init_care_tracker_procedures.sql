
DROP PROCEDURE IF EXISTS addCareTracker;
DROP PROCEDURE IF EXISTS addCareTrackerDrools;
DROP PROCEDURE IF EXISTS addCareTrackerIcd9Trigger;
DROP PROCEDURE IF EXISTS careTrackerAddItemGroupProcedure;
DROP PROCEDURE IF EXISTS careTrackerAddItemProcedure;

SET @creatorId = "-1"; -- system provider id

DELIMITER //

CREATE PROCEDURE addCareTracker(
    IN in_care_tracker_name varchar(255), IN in_description text, IN in_enabled tinyint(1))
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO `care_tracker` (care_tracker_name, description, system_managed, enabled, created_at, created_by, updated_at, updated_by)
    SELECT
        in_care_tracker_name AS care_tracker_name,
        in_description,
        TRUE AS system_managed,
        in_enabled AS enabled,
        NOW() AS created_at,
        @creatorId AS created_by,
        NOW() AS updated_at,
        @creatorId AS updated_by
    WHERE in_care_tracker_name NOT IN (
        SELECT care_tracker_name
        FROM care_tracker
        WHERE care_tracker_name = in_care_tracker_name
          AND system_managed IS TRUE
    );
END //

CREATE PROCEDURE addCareTrackerDrools(
    IN in_care_tracker_name varchar(255), IN in_drl_file varchar(255), IN in_drools_description text)
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO drools (drl_file, description, created_at, updated_at)
    SELECT in_drl_file,
           in_drools_description,
           NOW() AS created_at,
           NOW() AS updated_at
    WHERE in_drl_file NOT IN
          (
              SELECT drl_file
              FROM drools
              WHERE drl_file = in_drl_file
          );

    INSERT INTO care_tracker_drools
    SELECT drl.id,
           tracker.id
    FROM care_tracker tracker
             JOIN drools drl ON drl.drl_file = in_drl_file
    WHERE tracker.care_tracker_name = in_care_tracker_name
      AND system_managed IS TRUE
      AND tracker.id NOT IN (
        SELECT care_tracker_id
        FROM care_tracker_drools
        WHERE tracker.care_tracker_name = in_care_tracker_name
          AND system_managed IS TRUE
    );
END //

CREATE PROCEDURE addCareTrackerIcd9Trigger(
    IN in_care_tracker_name varchar(255), IN in_icd9_code varchar(7))
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO care_tracker_triggers_icd9(care_tracker_id, icd9_id)
    SELECT
        f.id,
        i.id
    FROM care_tracker f
             JOIN icd9 i ON i.icd9 = in_icd9_code
    WHERE f.care_tracker_name = in_care_tracker_name
      AND f.system_managed IS TRUE
      AND f.id NOT IN (
        SELECT care_tracker_id
        FROM care_tracker_triggers_icd9
                 JOIN icd9 icd_inner ON icd_inner.icd9 = in_icd9_code
        WHERE icd9_id = icd_inner.id
    );
END //


CREATE PROCEDURE careTrackerAddItemGroupProcedure(
    IN in_care_tracker_name varchar(255), IN in_group_name varchar(255), IN in_description text)
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO care_tracker_item_group(care_tracker_id, group_name, description, created_at, created_by, updated_at, updated_by)
    SELECT
        id,
        in_group_name,
        in_description,
        NOW() AS created_at,
        @creatorId AS created_by,
        NOW() AS updated_at,
        @creatorId AS updated_by
    FROM care_tracker f
    WHERE f.care_tracker_name=in_care_tracker_name
      AND f.system_managed IS TRUE
      AND f.id NOT IN (SELECT care_tracker_id FROM care_tracker_item_group g WHERE g.group_name = in_group_name);
END //


CREATE PROCEDURE careTrackerAddItemProcedure(
    IN in_group_name varchar(255), IN in_item_type varchar(255), IN in_item_code varchar(255), IN in_value_type varchar(255), IN in_value_label varchar(255), IN in_graphable tinyint(1), IN in_guideline text)
    SQL SECURITY INVOKER
BEGIN
    IF in_item_type = 'PREVENTION' THEN
        INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
        SELECT
            sheet.id,
            item_group.id,
            in_item_code,
            in_item_type,
            in_item_code,
            in_value_type,
            in_value_label,
            in_graphable,
            in_guideline AS guideline,
            NULL AS description,
            NOW() AS created_at,
            @creatorId AS created_by,
            NOW() AS updated_at,
            @creatorId AS updated_by
        FROM care_tracker sheet
                 JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
        WHERE item_group.group_name = in_group_name
          AND in_item_code NOT IN (
            SELECT item_type_code
            FROM care_tracker_item
            WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
        ) limit 1;
    ELSE
        INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
        SELECT
            sheet.id,
            item_group.id,
            type.typeDisplayName,
            in_item_type,
            type.type,
            in_value_type,
            in_value_label,
            in_graphable,
            in_guideline AS guideline,
            type.typeDescription AS description,
            NOW() AS created_at,
            @creatorId AS created_by,
            NOW() AS updated_at,
            @creatorId AS updated_by
        FROM care_tracker sheet
                 JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
                 JOIN measurementType type
        WHERE type.type IN (in_item_code)
          AND item_group.group_name = in_group_name
          AND type.type NOT IN (
            SELECT item_type_code
            FROM care_tracker_item
            WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
        ) limit 1;
    END IF;
END //

DELIMITER ;
