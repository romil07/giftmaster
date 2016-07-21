-- liquibase formatted sql

-- changeset rohan.ghosh:1

CREATE TABLE tiering_orchestrator_item_aud (
  id                       INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  request_id               VARCHAR(40)      NOT NULL,
  seller_id                VARCHAR(30)      NOT NULL,
  requested_by             VARCHAR(30)      NOT NULL,
  created_by               VARCHAR(30),
  requested_at             TIMESTAMP        NOT NULL,
  created_at               TIMESTAMP,
  updated_at               TIMESTAMP,
  updated_by               TIMESTAMP,
  tier                     VARCHAR(30)      NOT NULL,
  status                   VARCHAR(30)      NOT NULL,
  category                 VARCHAR(30)      NOT NULL,
  request_type             VARCHAR(30)      NOT NULL,
  metadata                 TEXT,
  rev int(11)                               NOT NULL,
  revtype tinyint(4)                    DEFAULT NULL,
  PRIMARY KEY (id, rev));
-- rollback drop TABLE tiering_orchestrator_item_aud