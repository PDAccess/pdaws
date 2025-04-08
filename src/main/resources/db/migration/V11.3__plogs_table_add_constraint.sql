ALTER TABLE ONLY plogs
    ADD CONSTRAINT plogs_session_id_foreign FOREIGN KEY (session_id) REFERENCES psessions(session_id);