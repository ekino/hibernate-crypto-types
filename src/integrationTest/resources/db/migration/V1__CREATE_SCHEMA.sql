CREATE TABLE person
(
    id                 UUID NOT NULL CONSTRAINT person_pkey PRIMARY KEY,
    name               TEXT,
    password           TEXT,
    private_data       TEXT,
    some_value         TEXT
);
