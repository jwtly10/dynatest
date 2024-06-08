CREATE TABLE IF NOT EXISTS test_suite_tb
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT,
    configuration TEXT,
    state         TEXT,
    created_at    TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc')),
    updated_at    TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc'))
);
drop table test_suite_meta_tb;
CREATE TABLE IF NOT EXISTS test_suite_meta_tb
(
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    test_suite_id        INTEGER UNIQUE,
    runs                 INTEGER DEFAULT 0,
    pass_count           INTEGER DEFAULT 0,
    fail_count           INTEGER DEFAULT 0,
    last_outcome         TEXT,
    last_finished_run_at TEXT,
    created_at           TEXT    DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc')),
    updated_at           TEXT    DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc')),
    FOREIGN KEY (test_suite_id) REFERENCES test_suite_tb (id)
);;

drop table test_suite_run_log_tb;
CREATE TABLE IF NOT EXISTS test_suite_run_log_tb
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    test_suite_id INTEGER,
    status        TEXT,
    start_time    TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc')),
    end_time      TEXT,
    duration      INTEGER,
    error_message TEXT,
    created_at    TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc')),
    updated_at    TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'utc')),
    FOREIGN KEY (test_suite_id) REFERENCES test_suite_tb (id)
);