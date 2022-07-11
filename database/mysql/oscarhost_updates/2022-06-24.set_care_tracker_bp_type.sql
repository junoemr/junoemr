UPDATE `care_tracker_item` cti JOIN care_tracker ct ON (ct.id = cti.care_tracker_id)
    SET cti.value_type='BLOOD_PRESSURE'
    WHERE ct.system_managed = '1'
    AND cti.item_type='MEASUREMENT'
    AND cti.item_type_code='BP'
