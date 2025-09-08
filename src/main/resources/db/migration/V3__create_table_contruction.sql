CREATE TABLE IF NOT EXISTS construction (
    construction_id   VARCHAR(255) PRIMARY KEY,
    user_id           VARCHAR(255) NOT NULL,
    user_name         VARCHAR(255),
    construction_name VARCHAR(255) NOT NULL,
    address           VARCHAR(500),
    phone_number      VARCHAR(50),
    zip_code          VARCHAR(20),
    mail              VARCHAR(255),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_construction_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
