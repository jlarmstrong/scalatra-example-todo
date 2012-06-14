CREATE TABLE PUBLIC.users (
                id INT AUTO_INCREMENT NOT NULL,
                email NVARCHAR(255) NOT NULL,
                password NVARCHAR(255) NOT NULL,
                first_name NVARCHAR(255) NOT NULL,
                last_name NVARCHAR(255) NOT NULL,
                rememberMe_token NVARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
);

CREATE TABLE PUBLIC.tasks (
                id INT AUTO_INCREMENT NOT NULL,
                owner_id INT NOT NULL,
                task NVARCHAR(255) NOT NULL,
                created_on DATETIME NOT NULL,
                completed_on DATETIME NOT NULL,
                PRIMARY KEY (id)
);