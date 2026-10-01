CREATE TABLE plants (
    id            UUID PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    species       VARCHAR(150),
    location      VARCHAR(150),
    acquired_date DATE,
    notes         VARCHAR(2000),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
