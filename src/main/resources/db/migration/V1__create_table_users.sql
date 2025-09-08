CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    pass_word VARCHAR(255),
    mail VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(255),
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_enabled BOOLEAN DEFAULT TRUE
);
