ALTER TABLE notification
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE notification
MODIFY message TEXT
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;