USE hyperativa;

CREATE TABLE role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
    name VARCHAR(100) COLLATE utf8_general_ci,
    password VARCHAR(100) COLLATE utf8_general_ci,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE batch (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    name varchar(30) not null, 
    description varchar(8) not null,     
    count INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE creditcard (
    id INT AUTO_INCREMENT PRIMARY KEY,
    line_id VARCHAR(1) COLLATE utf8_general_ci,
    number VARCHAR(50) COLLATE utf8_general_ci,
    card_number VARCHAR(50) COLLATE utf8_general_ci,
    id_batch INT,
    FOREIGN KEY (id_batch) REFERENCES batch(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

INSERT INTO role (name) values ('admin');
INSERT INTO role (name) values ('user');

INSERT INTO user (login, name, password, enabled, role_id) values ('admin', 'Administrador', '$2a$10$40amTr650p0q65VoqG71kOx9MbiJve/TTelYsTLi33Dyp.dmprGlu', TRUE, 1);