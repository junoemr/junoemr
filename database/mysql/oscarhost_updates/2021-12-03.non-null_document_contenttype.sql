UPDATE document SET contenttype='application/octet-stream' WHERE contenttype IS NULL;
ALTER TABLE document MODIFY COLUMN contenttype VARCHAR(255) NOT NULL;