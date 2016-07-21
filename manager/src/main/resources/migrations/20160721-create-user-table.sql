--liquibase formatted SQL

--changeset romil:1

CREATE TABLE `user` (
  `user_id` varchar(128) DEFAULT NULL,
  `friend_id` varchar(128) DEFAULT NULL,
  `relation_type` varchar(32) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--rollback DROP TABLE user