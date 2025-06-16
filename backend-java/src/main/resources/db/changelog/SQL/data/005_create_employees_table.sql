CREATE SEQUENCE employee_id_seq START 1 INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 NO CYCLE;

create TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    name    VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    location VARCHAR(50) NOT NULL,
    job_type_id BIGINT NOT NULL,
    job_position_id BIGINT NOT NULL,
    experience_years DECIMAL,
    expected_salary DECIMAL,
    availability_date DATE,
    education_level VARCHAR(50) NOT NULL,
    age BIGINT NOT NULL,
    status VARCHAR(100) NOT NULL,
    FOREIGN KEY (job_type_id) REFERENCES job_types(id) ON DELETE CASCADE,
    FOREIGN KEY (job_position_id) REFERENCES job_positions(id) ON DELETE CASCADE
);

