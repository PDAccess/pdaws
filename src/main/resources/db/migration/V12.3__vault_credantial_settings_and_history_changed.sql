ALTER TABLE auto_credantial_settings DROP is_active;
ALTER TABLE auto_credantial_settings ADD last_action TIMESTAMP;
ALTER TABLE auto_credantial_history DROP old_password;
ALTER TABLE auto_credantial_history DROP new_password;
ALTER TABLE auto_credantial_history DROP result;
ALTER TABLE auto_credantial_history RENAME COLUMN credantial_id to inventory_id;
ALTER TABLE auto_credantial_history ADD result BOOLEAN;
INSERT INTO system_settings (id, setting_tag, setting_value, setting_short_code, setting_category) VALUES
(34, 'default_password_length', '16', 'VADPL', 'vaultautosettings');
INSERT INTO system_settings (id, setting_tag, setting_value, setting_short_code, setting_category) VALUES
(35, 'ldap_password', '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz', 'VADPC', 'vaultautosettings');
INSERT INTO system_settings (id, setting_tag, setting_value, setting_short_code, setting_category) VALUES
(36, 'ldap_group_dn', '86400', 'VADPR', 'vaultautosettings');
INSERT INTO system_settings (id, setting_tag, setting_value, setting_short_code, setting_category) VALUES
(37, 'ldap_group_filter', 'day', 'VADPRT', 'vaultautosettings');


