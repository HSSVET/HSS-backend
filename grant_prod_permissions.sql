-- Production database için kullanıcı izinleri
GRANT ALL PRIVILEGES ON DATABASE hss_prod TO "hss-dev-user";
GRANT ALL ON ALL TABLES IN SCHEMA public TO "hss-dev-user";
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO "hss-dev-user";
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO "hss-dev-user";
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO "hss-dev-user";

