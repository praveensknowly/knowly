ALTER TABLE `user`
    MODIFY COLUMN `password` varchar(255) NULL,
    MODIFY COLUMN `number` varchar(255) NULL,
    ADD COLUMN `provider` varchar(20) NOT NULL DEFAULT 'LOCAL',
    ADD COLUMN `provider_id` varchar(255) NULL;