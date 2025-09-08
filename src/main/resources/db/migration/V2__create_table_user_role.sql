CREATE TABLE IF NOT EXISTS user_role (
    user_id     VARCHAR(255) NOT NULL,
    user_name   VARCHAR(255) NOT NULL,
    role_code   VARCHAR(100) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delete_time TIMESTAMP,
    is_active   BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (user_id, role_code),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
