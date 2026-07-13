-- Add attempts column to email_verification_token for brute force protection
ALTER TABLE email_verification_token ADD COLUMN attempts INT NOT NULL DEFAULT 0;
