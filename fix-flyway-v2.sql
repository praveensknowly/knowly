-- Run this SQL against your knowly database to fix the Flyway V2 description mismatch
-- This fixes the issue where V2 was recorded as 'add oauth columns.sql' due to double .sql extension
-- but the file is now named V2__add_oauth_columns.sql (single .sql)

UPDATE flyway_schema_history SET description = 'add oauth columns' WHERE version = '2';
