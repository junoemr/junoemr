INSERT IGNORE INTO provider (provider_no, last_name, first_name, provider_type, status)
VALUES ('99','Access','MyHealth','doctor', 1);

INSERT IGNORE INTO security (user_name, password, provider_no, pin)
VALUES ('myhealthaccess','-87-59-39-9261110-28126-65-88-70-4479109-63-12282-364342','99','1963');

INSERT IGNORE INTO property (name, value) VALUES ('aqs_api_secret_key', 'ZaQKiPuYAut7OmxiM2UX3KWMzkrA1beMZVLJZc1GbLyWp3oh');
INSERT IGNORE INTO property (name, value) VALUES ('aqs_organization_id', 'f71f2d9b-b1b3-44d7-ab1c-49325b09dc39');

INSERT IGNORE INTO integration (integration_type, remote_id, api_key) VALUES ('cloud_md', '9a38d5f9-2b89-437b-bf3f-d3de6cf6abef', 'KDb8UcOBi1MMDhRDlkPrDEwlLRnhQoXkQUc1zfTYLqjoZKvYLR2iFj3BhULlrStyTSEV1yAUd554
Z17G/z4CSZbDfTSK8XFPcMolBK42DuI=');


