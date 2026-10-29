CREATE TABLE users (
    id            UUID PRIMARY KEY,
    -- Stored lowercased, so uniqueness is case-insensitive.
    username      VARCHAR(32)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE user_sessions (
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    -- Hex SHA-256 of the bearer token; the raw token is never stored.
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_user_sessions_user_id ON user_sessions (user_id);

-- Plants created before accounts existed have no owner and are dropped.
DELETE FROM plants;

ALTER TABLE plants ADD COLUMN owner_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX idx_plants_owner_id ON plants (owner_id);
