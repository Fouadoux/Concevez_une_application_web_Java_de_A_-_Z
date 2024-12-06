USE transactions_db;

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `app_account` (`account_id`, `user_id`, `balance`, `last_update`, `created_at`, `daily_limit`)
VALUES
(1, 1, 100000, '2024-11-28 14:55:37', '2024-11-28 15:55:37.353632', 50000);

INSERT INTO `roles` (`role_id`, `role_name`)
VALUES
(1, 'ADMIN'),
(2, 'USER');

INSERT INTO `users` (`id`, `username`, `email`, `password`, `created_at`, `role_id`)
VALUES
(1, 'admin', 'admin@example.fr', '$2a$10$0stGsueLWmaaIgNehp9pi.LZtuS6Fy2nXcK.PIJPbMeH73yq3CWbW', '2024-11-28 14:55:37', 1);

INSERT INTO `transactions_fee` (`fee_id`, `effective_date`, `percentage`)
VALUES
(1, '2024-11-28 00:00:00', 5000);

SET FOREIGN_KEY_CHECKS = 1;
