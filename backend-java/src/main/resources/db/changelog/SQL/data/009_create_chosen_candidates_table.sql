CREATE TABLE chosen_candidates (
    selection_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    PRIMARY KEY (selection_id, employee_id),
    CONSTRAINT fk_selection FOREIGN KEY (selection_id) REFERENCES selections(id) ON DELETE CASCADE,
    CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
