-- Add composite indexes for scheduler queries on help_session
CREATE INDEX idx_help_session_status_created ON help_session (status, created_at);
CREATE INDEX idx_help_session_status_expires ON help_session (status, session_expires_at);

-- Fix message column from tinytext to TEXT to support longer messages
ALTER TABLE message MODIFY COLUMN message TEXT NOT NULL;
