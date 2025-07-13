CREATE TABLE IF NOT EXISTS user_profile (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    email_address VARCHAR(255),
    street_address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip_code VARCHAR(20)
);
