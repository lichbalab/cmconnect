--liquibase formatted sql
--changeset kiryl:1

CREATE SEQUENCE certificate_seq
    INCREMENT 1
    START WITH 1
    MINVALUE 1
    CACHE 1;

CREATE TABLE certificate (
    id BIGINT NOT NULL,
    alias VARCHAR(255),
    serial_number VARCHAR(255),
    expiration_date TIMESTAMP WITH TIME ZONE,
    subject VARCHAR(255),
    issuer VARCHAR(255),
    certificate_chain_data BYTEA, -- BYTEA is used for binary data
    private_key_data BYTEA,
    PRIMARY KEY (id)
);