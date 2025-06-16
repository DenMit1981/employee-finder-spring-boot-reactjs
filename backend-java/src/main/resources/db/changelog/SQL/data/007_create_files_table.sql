CREATE SEQUENCE file_id_seq START 1 INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 NO CYCLE;

create TABLE IF NOT EXISTS files (
    id BIGSERIAL   PRIMARY KEY NOT NULL,
    name           VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    path_file      VARCHAR(255) NOT NULL,
    file_size      BIGINT,
    resume_id      BIGINT NOT NULL,
    FOREIGN KEY (resume_id) REFERENCES resumes(id)
);