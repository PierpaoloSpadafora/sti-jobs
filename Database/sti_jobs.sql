CREATE TABLE Users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE Machines (
    machine_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    status TEXT DEFAULT 'DISPONIBILE',  -- Stato del macchinario (es. 'DISPONIBILE', 'IN MANUTENZIONE')
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Jobs (
    job_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT DEFAULT 'DA COMPLETARE',
    assignee_id INTEGER,
    priority TEXT,  -- PRIORITÀ GENERALE ('ORARIO', 'MACCHINARIO', 'SCADENZA', ecc.)
    due_date DATETIME,  -- SCADENZA DEL JOB
    machine_id INTEGER,  -- ID DEL MACCHINARIO RICHIESTO PER IL JOB (se applicabile)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignee_id) REFERENCES Users(user_id),
    FOREIGN KEY (machine_id) REFERENCES Machines(machine_id)
);

CREATE TABLE JobCharacteristics (
    characteristic_id INTEGER PRIMARY KEY AUTOINCREMENT,
    job_id INTEGER,
    characteristic_name TEXT NOT NULL,  -- Nome della caratteristica (es. 'ORARIO', 'SCADENZA', 'MACCHINARIO')
    characteristic_value TEXT,  -- Valore specifico della caratteristica (es. 'Alta', '11/11/2024', 'Macchinario A')
    FOREIGN KEY (job_id) REFERENCES Jobs(job_id) ON DELETE CASCADE
);

CREATE TABLE Schedules (
    schedule_id INTEGER PRIMARY KEY AUTOINCREMENT,
    job_id INTEGER NOT NULL,
    machine_id INTEGER NOT NULL,
    start_time DATETIME NOT NULL,  -- Ora di inizio del job sul macchinario
    end_time DATETIME,  -- Ora di fine del job sul macchinario (se applicabile)
    FOREIGN KEY (job_id) REFERENCES Jobs(job_id) ON DELETE CASCADE,
    FOREIGN KEY (machine_id) REFERENCES Machines(machine_id) ON DELETE CASCADE
);
