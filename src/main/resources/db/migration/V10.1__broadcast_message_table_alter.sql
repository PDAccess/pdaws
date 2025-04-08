ALTER TABLE broadcastmessage DROP start_date;
ALTER TABLE broadcastmessage DROP end_date;
ALTER TABLE broadcastmessage DROP start_time;
ALTER TABLE broadcastmessage DROP end_time;
ALTER TABLE broadcastmessage ADD start_date TIMESTAMP;
ALTER TABLE broadcastmessage ADD end_date TIMESTAMP;