ALTER TABLE clinic
  ADD COLUMN clinic_billing_address_id INT DEFAULT NULL,
  ADD FOREIGN KEY (clinic_billing_address_id) REFERENCES clinic_billing_address(id);
