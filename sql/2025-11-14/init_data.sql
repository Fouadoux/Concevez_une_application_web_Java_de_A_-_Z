-- Script pour insérer des données dans transactions_db
USE transactions_db;

SET FOREIGN_KEY_CHECKS = 0;

-- Insérer des données dans la table app_account
INSERT INTO `app_account` (`account_id`, `user_id`, `balance`, `last_update`, `created_at`)
VALUES
(1, 1, 100000, '2024-11-28 14:55:37', '2024-11-28 15:55:37.353632');

-- Insérer des données dans la table roles
INSERT INTO `roles` (`role_id`, `role_name`, `daily_limit`)
VALUES
(1, 'ADMIN', 500000),
(2, 'USER', 250000);

-- Insérer des données dans la table users
INSERT INTO `users` (`id`, `username`, `email`, `password`, `created_at`, `role_id`)
VALUES
(1, 'admin', 'admin@example.fr', '$2a$10$0stGsueLWmaaIgNehp9pi.LZtuS6Fy2nXcK.PIJPbMeH73yq3CWbW', '2024-11-28 14:55:37', 1);

-- Insérer des données dans la table transactions_fee
INSERT INTO `transactions_fee` (`fee_id`, `effective_date`, `percentage`)
VALUES
(1, '2024-11-28 00:00:00', 5000);

SET FOREIGN_KEY_CHECKS = 1;
