INSERT INTO issue (`code`, description, role, update_date, priority, `type`, sortOrderId)
SELECT
  ich.ichppccode,
  ich.description,
  'doctor',
  CURDATE(),
  NULL,
  'ICHPPC',
  0
FROM ichppccode ich
WHERE ich.ichppccode NOT IN (SELECT `code` FROM issue WHERE type='ICHPPC')
;