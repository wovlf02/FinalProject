DROP TABLE IF EXISTS exam_schedule;

CREATE TABLE exam_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    subject VARCHAR(50),
    exam_date DATE NOT NULL,
    description VARCHAR(500),
    location VARCHAR(200),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
); 