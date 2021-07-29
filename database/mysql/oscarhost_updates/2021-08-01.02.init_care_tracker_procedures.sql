
DROP PROCEDURE IF EXISTS careTrackerAddItemGroupProcedure;
DROP PROCEDURE IF EXISTS careTrackerAddItemProcedure;

SET @creatorId = "-1"; -- system provider id

DELIMITER //

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
