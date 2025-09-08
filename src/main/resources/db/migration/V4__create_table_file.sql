CREATE TABLE IF NOT EXISTS files (
    uuid            UUID PRIMARY KEY,
    user_id         VARCHAR(255) NOT NULL,
    construction_id VARCHAR(255) NOT NULL,
    user_name       VARCHAR(255),
    file_name       VARCHAR(500) NOT NULL,
    file_size       BIGINT,
    file_path       VARCHAR(1000) NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_file_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_file_construction FOREIGN KEY (construction_id) REFERENCES construction(construction_id)
);