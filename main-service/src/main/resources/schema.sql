drop table IF EXISTS events;
drop table IF EXISTS users;
drop table IF EXISTS categories;
drop table IF EXISTS locations;

create TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT uq_user_email UNIQUE (email)
);

create TABLE IF NOT EXISTS categories (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT uq_categorie_name UNIQUE (name)
);

create TABLE IF NOT EXISTS locations (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  lat FLOAT NOT NULL,
  lon FLOAT NOT NULL,
  CONSTRAINT uq_location UNIQUE (lat, lon)
);

create TABLE IF NOT EXISTS events (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  annotation VARCHAR(2000) NOT NULL,
  category_id BIGINT NOT NULL,
  confirmed_requests BIGINT,
  created_on TIMESTAMP WITHOUT TIME ZONE,
  description VARCHAR(7000),
  event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  initiator_id BIGINT NOT NULL,
  location_id BIGINT NOT NULL,
  paid BOOL NOT NULL,
  participant_limit INTEGER,
  published_on TIMESTAMP WITHOUT TIME ZONE,
  request_moderation BOOL,
  state VARCHAR(10),
  title VARCHAR(120) NOT NULL,
  views BIGINT NOT NULL,
  CONSTRAINT fk_events_to_categories FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE,
  CONSTRAINT fk_events_to_users FOREIGN KEY(initiator_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_events_to_locations FOREIGN KEY(location_id) REFERENCES locations(id)
);