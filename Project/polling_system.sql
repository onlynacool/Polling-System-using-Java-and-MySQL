CREATE DATABASE polling_system;
USE polling_system;

CREATE TABLE users (
    aadhaar_number VARCHAR(14) NOT NULL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    has_voted BOOLEAN DEFAULT FALSE,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_aadhaar VARCHAR(14),
    vote_option VARCHAR(50),
    FOREIGN KEY (user_aadhaar) REFERENCES users(aadhaar_number)
);
select * from users;
select * from votes;