-- Store browser push notification subscriptions per user
CREATE TABLE `push_subscription` (
    `id` VARCHAR(36) NOT NULL,
    `user_profile_id` VARCHAR(36) NOT NULL,
    `endpoint` VARCHAR(1000) NOT NULL,
    `p256dh_key` VARCHAR(255) NOT NULL,
    `auth_key` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_push_subscription_endpoint` (`endpoint`(768)),
    CONSTRAINT `fk_push_subscription_user_profile` FOREIGN KEY (`user_profile_id`) REFERENCES `user_profile` (`id`) ON DELETE CASCADE
);

CREATE INDEX `idx_push_subscription_user_profile` ON `push_subscription` (`user_profile_id`);
