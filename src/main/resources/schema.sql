CREATE TABLE IF NOT EXISTS test_suite_tb
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT,
    configuration TEXT,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
);