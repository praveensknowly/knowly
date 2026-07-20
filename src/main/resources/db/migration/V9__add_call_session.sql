CREATE TABLE `call_session` (
  `id` VARCHAR(36) NOT NULL,
  `help_session_id` VARCHAR(36) NOT NULL,
  `caller_id` VARCHAR(36) NOT NULL,
  `callee_id` VARCHAR(36) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `initiated_at` DATETIME NOT NULL,
  `connected_at` DATETIME NULL,
  `ended_at` DATETIME NULL,
  `duration_seconds` BIGINT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_call_help_session` FOREIGN KEY (`help_session_id`) REFERENCES `help_session` (`session_id`),
  CONSTRAINT `fk_call_caller` FOREIGN KEY (`caller_id`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `fk_call_callee` FOREIGN KEY (`callee_id`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB;
