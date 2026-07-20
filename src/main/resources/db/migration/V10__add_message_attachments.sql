ALTER TABLE message
  ADD COLUMN attachment_path VARCHAR(255) NULL,
  ADD COLUMN attachment_original_name VARCHAR(255) NULL,
  ADD COLUMN attachment_mime_type VARCHAR(100) NULL,
  ADD COLUMN attachment_size BIGINT NULL,
  MODIFY COLUMN message TEXT NULL;
