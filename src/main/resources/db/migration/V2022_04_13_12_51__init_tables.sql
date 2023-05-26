CREATE TABLE tasks_queue
(
    id                 BIGSERIAL    NOT NULL,
    initiator_username varchar(255) NOT NULL,
    lab_id             int8         NOT NULL,
    status             VARCHAR(255) NOT NULL DEFAULT 'CREATED',
    expiration_date    timestamp,
    creation_date      timestamp    NOT NULL DEFAULT NOW(),
    stand_id           int8         NOT NULL,
    execute_date       timestamp,
    role_username      VARCHAR(255) NOT NULL,
    duration           int8         NOT NULL,
    file_path          VARCHAR(255) NOT NULL,
    usb_port           VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);