ALTER TABLE `billingvisit` ADD PRIMARY KEY IF NOT EXISTS (`visittype`);
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("B", "Community Health Centre", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("J", "First Nations Primary Health Care Clinic", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("K", "Hybrid Primary Care Practice (part-time longitudinal practice, part-time walk-in clinic)", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("L", "Longitudinal Primary Care Practice (e.g. GP family practice or PCN clinic)", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("N", "Health Care Practitioner Office (non-physician)", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("Q", "Specialist Physician Office", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("U", "Urgent and Primary Care Centre", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("V", "Virtual Care Clinic", "BC");
INSERT IGNORE INTO billingvisit (`visittype`, `visit_desc`, `region`) VALUES ("W", "Walk-In Clinic", "BC");

