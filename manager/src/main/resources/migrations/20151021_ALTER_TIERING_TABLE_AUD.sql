--liquibase formatted SQL

--changeset rohan:2

ALTER TABLE tiering_orchestrator_item_aud
MODIFY request_id      VARCHAR(128)      NOT NULL;

--rollback ALTER TABLE tiering_orchestrator_item MODIFY request_id VARCHAR(40) NOT NULL;