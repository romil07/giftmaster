--liquibase formatted SQL

--changeset romil:1
CREATE TABLE `user_vertical` (
  `user_type` varchar(32) DEFAULT NULL,
  `vertical` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--rollback DROP TABLE user_vertical