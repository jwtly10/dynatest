CREATE TABLE IF NOT EXISTS test_suite_tb
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT,
    configuration TEXT,
    created_at    TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc')),
    updated_at    TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc'))
);