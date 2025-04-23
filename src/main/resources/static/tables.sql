CREATE TABLE chapo_conta (
                       ID INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                       AGENCIA INT UNSIGNED NOT NULL,
                       CONTA BIGINT UNSIGNED NOT NULL,
                       SALDO DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
                       INDEX (AGENCIA, CONTA)
);

INSERT INTO chapo_conta(AGENCIA, CONTA, SALDO) VALUES(1,123,0.00);