CREATE TABLE IF NOT EXISTS EMPLOYEE
(
    id UUID default random_uuid(),
    name  VARCHAR(255),
    email VARCHAR(255),
    PRIMARY KEY (id)
);
