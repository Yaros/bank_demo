
INSERT INTO users (name) VALUES ('Example User');

INSERT INTO accounts (user_id, currency, balance) VALUES (1, 'EUR', 1000.00);
INSERT INTO accounts (user_id, currency, balance) VALUES (1, 'USD', 1500.00);
INSERT INTO accounts (user_id, currency, balance) VALUES (1, 'SEK', 20000.00);
INSERT INTO accounts (user_id, currency, balance) VALUES (1, 'GBP', 800.00);
INSERT INTO accounts (user_id, currency, balance) VALUES (1, 'VND', 23000000);


INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP);

INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '1' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '2' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '3' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '4' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_IN', 1000.00, 2000.00, 'c16c2537-75be-42e6-8c00-5b3fad742ac1', CURRENT_TIMESTAMP + INTERVAL '5' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_OUT', 1000.00, 1000.00, '4d4c108e-d83b-4047-ae12-dce2fdd52483', CURRENT_TIMESTAMP + INTERVAL '6' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_IN', 1000.00, 2000.00, 'c16c2537-75be-42e6-8c00-5b3fad742ac1', CURRENT_TIMESTAMP + INTERVAL '7' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_OUT', 1000.00, 1000.00, '4d4c108e-d83b-4047-ae12-dce2fdd52483', CURRENT_TIMESTAMP + INTERVAL '8' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_IN', 1000.00, 2000.00, 'c16c2537-75be-42e6-8c00-5b3fad742ac1', CURRENT_TIMESTAMP + INTERVAL '9' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_OUT', 1000.00, 1000.00, '4d4c108e-d83b-4047-ae12-dce2fdd52483', CURRENT_TIMESTAMP + INTERVAL '10' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '11' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '12' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '13' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '14' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '15' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '16' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '17' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '18' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '19' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '20' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '21' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '22' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '23' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '24' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '25' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '26' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_IN', 1000.00, 2000.00, 'c16c2537-75be-42e6-8c00-5b3fad742ac1', CURRENT_TIMESTAMP + INTERVAL '27' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_OUT', 1000.00, 1000.00, '4d4c108e-d83b-4047-ae12-dce2fdd52483', CURRENT_TIMESTAMP + INTERVAL '28' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_IN', 1000.00, 2000.00, 'c16c2537-75be-42e6-8c00-5b3fad742ac1', CURRENT_TIMESTAMP + INTERVAL '29' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'EXCHANGE_OUT', 1000.00, 1000.00,'4d4c108e-d83b-4047-ae12-dce2fdd52483', CURRENT_TIMESTAMP + INTERVAL '30' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '31' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '32' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEPOSIT', 1000.00, 2000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '33' SECOND);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (1, 'DEBIT', 1000.00, 1000.00, NULL, CURRENT_TIMESTAMP + INTERVAL '34' SECOND);


INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (2, 'DEPOSIT', 1500.00, 1500.00, NULL, CURRENT_TIMESTAMP);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (3, 'DEPOSIT', 20000.00, 20000.00, NULL, CURRENT_TIMESTAMP);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (4, 'DEPOSIT', 800.00, 800.00, NULL, CURRENT_TIMESTAMP);
INSERT INTO transactions (account_id, type, amount, balance_after, reference_id, created_at) VALUES (5, 'DEPOSIT', 23000000, 23000000, NULL, CURRENT_TIMESTAMP);

