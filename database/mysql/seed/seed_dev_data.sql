--Add MHA provider
INSERT IGNORE INTO provider (provider_no, last_name, first_name, provider_type, status)
VALUES ('99','Access','MyHealth','doctor', 1);

--Add Cloudmd provider
INSERT IGNORE INTO provider (provider_no, last_name, first_name, provider_type, status)
VALUES ('199','Provider','Cloudmd','doctor', 1);

--Add roles from MHA provider
INSERT IGNORE INTO secUserRole (sec_role_id, provider_no, role_name, activeyn, lastUpdateDate)
VALUES (3, '99', 'admin', 1, NOW());
INSERT IGNORE INTO secUserRole (sec_role_id, provider_no, role_name, activeyn, lastUpdateDate)
VALUES (2, '99', 'doctor', 1, NOW());

--Add roles from Cloudmd provider
INSERT IGNORE INTO secUserRole (sec_role_id, provider_no, role_name, activeyn, lastUpdateDate)
VALUES (3, '199', 'admin', 1, NOW());
INSERT IGNORE INTO secUserRole (sec_role_id, provider_no, role_name, activeyn, lastUpdateDate)
VALUES (2, '199', 'doctor', 1, NOW());

--Set primary role for MHA provider
INSERT IGNORE INTO program_provider (program_id, provider_no, role_id)
VALUES (10034, '99', 2);

--Set primary role for Cloudmd provider
INSERT IGNORE INTO program_provider (program_id, provider_no, role_id)
VALUES (10034, '199', 2);

--Add log in record for MHA user
INSERT IGNORE INTO security (user_name, password, provider_no, pin)
VALUES ('myhealthaccess','-87-59-39-9261110-28126-65-88-70-4479109-63-12282-364342','99','1963');

--Add AQS key and organization id
INSERT IGNORE INTO property (name, value) VALUES ('aqs_api_secret_key', 'ZaQKiPuYAut7OmxiM2UX3KWMzkrA1beMZVLJZc1GbLyWp3oh');
INSERT IGNORE INTO property (name, value) VALUES ('aqs_organization_id', 'f71f2d9b-b1b3-44d7-ab1c-49325b09dc39');

--Add Cloudmd site
INSERT IGNORE INTO site (name, short_name, bg_color, province, status)
VALUES ('CloudMD', 'CMD', '#2BC6C6', 'BC', 1);

--Add MHA site
INSERT IGNORE INTO site (name, short_name, bg_color, province, status)
VALUES ('MyHealthAccess', 'MHA', '#4D73BF', 'BC', 1);

--Add Kii site
INSERT IGNORE INTO site (name, short_name, bg_color, province, status)
VALUES ('Kii', 'KII', '#365D62', 'BC', 1);

--Cloudmd clinic integration
INSERT IGNORE INTO integration (integration_type, remote_id, api_key, site_id) VALUES ('cloud_md', '9a38d5f9-2b89-437b-bf3f-d3de6cf6abef', 'KDb8UcOBi1MMDhRDlkPrDEwlLRnhQoXkQUc1zfTYLqjoZKvYLR2iFj3BhULlrStyTSEV1yAUd554
Z17G/z4CSZbDfTSK8XFPcMolBK42DuI=', (SELECT site_id FROM site WHERE name = 'CloudMD'));

--MHA clinic integration
INSERT IGNORE INTO integration (integration_type, remote_id, api_key, site_id) VALUES ('my_health_access', 'fa120156-47b7-4cc4-b97c-9a1f768e7b92', 'vQPSTIR7xKq6kouzDd2FVeBUw0zbixKKJDWlI8VuZttlScyNJlAhzDAIB8p2aka2fnWlQLZjqVhm
k6GgQW+tx+wYsYSUePMpDRIINPmuJW4=', (SELECT site_id FROM site WHERE name = 'MyHealthAccess'));

--Kii clinic integration
INSERT IGNORE INTO integration (integration_type, remote_id, api_key, site_id) VALUES ('my_health_access', '894dd73f-91f1-4ea5-b636-50113350160d', 'YnGEyIlWfZpzxtYimaPJh0bZ/KilapBp+AyD4udZm/LsCrrk0pcmgAbz7/E5K0grmYpy5ETDAGMZ
XsefVe//cdEmKfpvNxB6dS5wOEU6be0=', (SELECT site_id FROM site WHERE name = 'Kii'));
