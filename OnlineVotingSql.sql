-- Create the database
 CREATE DATABASE IF NOT EXISTS voting_system;

-- Use the database
USE voting_system;

-- Create the candidates table
CREATE TABLE IF NOT EXISTS candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    party_symbol VARCHAR(50),
    votes INT
);

-- Insert sample candidates into the table (with IGNORE to avoid duplicates)
INSERT IGNORE INTO candidates (name, party_symbol, votes) VALUES
  ('Bhartiya Janta Party','Lotus', 0),
    ('Aam Aadmi Party','Broom', 0),
    ('Congress','Hand', 0),
    ('Samajwadi Party','Cycle', 0);
    SELECT * FROM candidates ;
SHOW DATABASES ;
DROP DATABASE voting_system;