UPDATE system_settings SET setting_value=0 WHERE setting_tag='two_factor_auth';
UPDATE system_settings SET setting_value='/service/nav/list' WHERE setting_tag='home_page_url';
UPDATE system_settings SET setting_value='' WHERE setting_tag='after_sign_out_path';