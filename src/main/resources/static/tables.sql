CREATE TABLE chapo_conta (
                       ID INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                       AGENCIA INT UNSIGNED NOT NULL,
                       CONTA BIGINT UNSIGNED NOT NULL,
                       SALDO DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
                       INDEX (AGENCIA, CONTA)
);

CREATE TABLE chapo_lancamento (
                            ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                            AGENCIA INT NOT NULL,
                            CONTA BIGINT NOT NULL,
                            VALOR DECIMAL(10, 2) NOT NULL, -- Assuming a precision of 10 and 2 decimal places
                            HISTORICO INT NOT NULL,
                            TIPO VARCHAR(255) NOT NULL -- Assuming a reasonable max length for the enum string
);

INSERT INTO chapo_conta(AGENCIA, CONTA, SALDO) VALUES(1,123,0.00);