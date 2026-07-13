-- Flyway migration V1__init_schema.sql
-- Generated from mysqldump --no-data against the Hibernate-generated schema.

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `certification`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certification` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `credential_url` varchar(1000) DEFAULT NULL,
  `issuer` varchar(150) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `year` int DEFAULT NULL,
  `profile_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2yisx03xrsn33iqe00orlq76s` (`profile_id`),
  CONSTRAINT `FK2yisx03xrsn33iqe00orlq76s` FOREIGN KEY (`profile_id`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `education`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `education` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `degree` varchar(120) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `end_year` int DEFAULT NULL,
  `field_of_study` varchar(120) DEFAULT NULL,
  `institution` varchar(150) NOT NULL,
  `start_year` int DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `profile_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfeh4dc1bgsxrg6xwqqy48rnb3` (`profile_id`),
  CONSTRAINT `FKfeh4dc1bgsxrg6xwqqy48rnb3` FOREIGN KEY (`profile_id`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_verification_token`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_verification_token` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `verified` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `help_session`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `help_session` (
  `session_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `ended_at` datetime(6) DEFAULT NULL,
  `expired_reason` varchar(255) DEFAULT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `first_expert_reply_at` datetime(6) DEFAULT NULL,
  `rated_at` datetime(6) DEFAULT NULL,
  `session_expires_at` datetime(6) DEFAULT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `subject` varchar(150) DEFAULT NULL,
  `version` bigint NOT NULL,
  `helper` varchar(255) DEFAULT NULL,
  `requester` varchar(255) DEFAULT NULL,
  `skill_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`session_id`),
  KEY `FK2bwha1uuh4v2g8qpa7p91b8d9` (`helper`),
  KEY `FK5py7a9dm5cob7bjylu9j9tqrh` (`requester`),
  KEY `FKtii5xvxfeebfp6d05i3vu34mn` (`skill_id`),
  CONSTRAINT `FK2bwha1uuh4v2g8qpa7p91b8d9` FOREIGN KEY (`helper`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `FK5py7a9dm5cob7bjylu9j9tqrh` FOREIGN KEY (`requester`) REFERENCES `user_profile` (`id`),
  CONSTRAINT `FKtii5xvxfeebfp6d05i3vu34mn` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`id`),
  CONSTRAINT `help_session_chk_1` CHECK ((`status` in (_utf8mb4'Active',_utf8mb4'Ended',_utf8mb4'Expired',_utf8mb4'Pending',_utf8mb4'Ignored')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `language`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `language` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg8hr207ijpxlwu10pewyo65gv` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `message_id` varchar(255) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `message` tinytext NOT NULL,
  `sent_at` datetime(6) NOT NULL,
  `type` enum('Code','File','Image','Text','Voice') DEFAULT NULL,
  `sender_id` varchar(255) DEFAULT NULL,
  `session_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `FKkqbyufjgk44k5472f9i9na5d1` (`sender_id`),
  KEY `FK4v483ymdj8pr0tqvo583ltx2w` (`session_id`),
  CONSTRAINT `FK4v483ymdj8pr0tqvo583ltx2w` FOREIGN KEY (`session_id`) REFERENCES `help_session` (`session_id`),
  CONSTRAINT `FKkqbyufjgk44k5472f9i9na5d1` FOREIGN KEY (`sender_id`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `link` varchar(300) DEFAULT NULL,
  `role` varchar(120) NOT NULL,
  `technologies` varchar(250) DEFAULT NULL,
  `title` varchar(140) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `profile_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmsqf1fquayfwt4ql5ygm6y40q` (`profile_id`),
  CONSTRAINT `FKmsqf1fquayfwt4ql5ygm6y40q` FOREIGN KEY (`profile_id`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rating`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rating` (
  `rating_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `review` varchar(1000) DEFAULT NULL,
  `stars` int DEFAULT NULL,
  `session_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rating_id`),
  UNIQUE KEY `UKh55uh8ugxsu2hs8pvykj8hx8m` (`session_id`),
  CONSTRAINT `FKep5m0q82hvmwoxo6xl15mhxk1` FOREIGN KEY (`session_id`) REFERENCES `help_session` (`session_id`),
  CONSTRAINT `rating_chk_1` CHECK (((`stars` <= 5) and (`stars` >= 1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skill`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skill` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(100) NOT NULL,
  `proficiency_level` enum('ADVANCED','BEGINNER','EXPERT','INTERMEDIATE') DEFAULT NULL,
  `search_key` varchar(255) DEFAULT NULL,
  `skill_score` double NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `years_of_experience` double NOT NULL,
  `profile_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_skill_name` (`name`),
  KEY `idx_skill_search_key` (`search_key`),
  KEY `idx_skill_score` (`skill_score`),
  KEY `FKes2avvi6c1ei8i4v3n6uny0n7` (`profile_id`),
  CONSTRAINT `FKes2avvi6c1ei8i4v3n6uny0n7` FOREIGN KEY (`profile_id`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `number` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK32852vmffyhhg5ov56amkcx7s` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_profile`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile` (
  `id` varchar(255) NOT NULL,
  `bio` varchar(1000) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `overall_rating` double NOT NULL,
  `profile_picturepath` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKebc21hy5j7scdvcjt0jy6xxrv` (`user_id`),
  CONSTRAINT `FK6kwj5lk78pnhwor4pgosvb51r` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_profile_chk_1` CHECK ((`gender` in (_utf8mb4'OTHER',_utf8mb4'PREFER_NOT_TO_SAY',_utf8mb4'MALE',_utf8mb4'FEMALE')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_profile_languages`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile_languages` (
  `profile_id` varchar(255) NOT NULL,
  `language_id` varchar(255) NOT NULL,
  PRIMARY KEY (`profile_id`,`language_id`),
  KEY `FK1i0kwhd6dw35faux1enyl6ipq` (`language_id`),
  CONSTRAINT `FK1i0kwhd6dw35faux1enyl6ipq` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `FKi4n7gsnkgtjmaq70yinwy7jqw` FOREIGN KEY (`profile_id`) REFERENCES `user_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;