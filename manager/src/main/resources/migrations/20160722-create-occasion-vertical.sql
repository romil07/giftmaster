--liquibase formatted SQL

--changeset romil:1

CREATE TABLE `occasion_vertical` (
  `occasion` varchar(32) DEFAULT NULL,
  `vertical` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--rollback DROP TABLE occasion_vertical