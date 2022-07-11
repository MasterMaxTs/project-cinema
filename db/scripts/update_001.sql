CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    email    VARCHAR NOT NULL,
    phone    VARCHAR NOT NULL,
    CONSTRAINT email_phone_unique UNIQUE (email, phone)
)