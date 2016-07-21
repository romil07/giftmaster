--liquibase formatted SQL

--changeset romil:1

CREATE TABLE `event` (
  `user_id` varchar(128) DEFAULT NULL,
  `event` varchar(128) DEFAULT NULL,
  `date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--rollback DROP TABLE event;