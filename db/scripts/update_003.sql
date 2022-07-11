CREATE TABLE tickets
(
    id         SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES sessions (id),
    pos_row    INT NOT NULL,
    cell       INT NOT NULL,
    user_id    INT NOT NULL REFERENCES users (id),
    CONSTRAINT row_cell_session_id_unique UNIQUE (pos_row, cell, session_id)
);