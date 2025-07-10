DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS participation_requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY,
    annotation         TEXT                        NOT NULL,
    category_id        BIGINT                      NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description        TEXT                        NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT                      NOT NULL,
    location_id        BIGINT                      NOT NULL,
    paid               BOOLEAN DEFAULT false       NOT NULL,
    participant_limit  INTEGER DEFAULT 0,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state              VARCHAR(100)                NOT NULL,
    title              VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT fk_events_category_id FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_events_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_events_location_id FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY,
    pinned BOOLEAN DEFAULT false NOT NULL,
    title  VARCHAR(50)           NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    event_id       BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT pk_compilation_events PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT fk_compilation_events_event_id FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_compilation_events_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (id)
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    event_id     BIGINT                      NOT NULL,
    requester_id BIGINT                      NOT NULL,
    status       VARCHAR(100)                NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_participation_requests PRIMARY KEY (id),
    CONSTRAINT fk_participation_requests_event_id FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_participation_requests_requester_id FOREIGN KEY (requester_id) REFERENCES users (id)
);