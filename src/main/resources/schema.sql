SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    birth_date DATE,
    experience_level VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    role_id INT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    stripe_id VARCHAR(255),
    FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

-- 山マスタ
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS mountains;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS mountains (
    mountain_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100),
    name_kana VARCHAR(100),
    elevation INT NOT NULL,
    prefecture VARCHAR(100),
    latitude DECIMAL(9, 6),
    longitude DECIMAL(9, 6),
    typical_distance_km DECIMAL(5, 2),
    typical_duration_minutes INT,
    typical_elevation_gain INT,
    difficulty VARCHAR(20),
    description TEXT,
    is_hyakumeizan BOOLEAN DEFAULT FALSE,
    image_url VARCHAR(255),
    image_citation VARCHAR(255),
    CONSTRAINT uk_mountains_name_prefecture UNIQUE (name, prefecture)
);

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS activity_images;

DROP TABLE IF EXISTS activity_details;

DROP TABLE IF EXISTS activities;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS activities (
    activity_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    activity_date DATE NOT NULL,
    mountain_id INT,
    location VARCHAR(100),
    description TEXT,
    image_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (mountain_id) REFERENCES mountains (mountain_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS activity_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    activity_id INT NOT NULL,
    distance_km DECIMAL(5, 2),
    duration_minutes INT,
    elevation_gain INT,
    max_elevation INT,
    pace_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (activity_id) REFERENCES activities (activity_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS verification_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mountain_notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mountain_id INT NOT NULL,
    user_id INT NOT NULL,
    title TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (mountain_id) REFERENCES mountains (mountain_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS note_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    note_id INT NOT NULL,
    item_name TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES mountain_notes (id) ON DELETE CASCADE
);