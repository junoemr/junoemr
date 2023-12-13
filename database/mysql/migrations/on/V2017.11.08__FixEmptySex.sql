-- Fix demographic records with sex = "" (Empty string) by setting sex to "U" (Undefined)
UPDATE demographic SET sex = "U" WHERE sex = "";
