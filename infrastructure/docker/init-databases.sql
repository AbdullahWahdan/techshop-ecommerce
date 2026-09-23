-- Create separate databases for each relational microservice
CREATE DATABASE user_db;
CREATE DATABASE order_db;
CREATE DATABASE inventory_db;

-- Grant permissions to default postgres user
GRANT ALL PRIVILEGES ON DATABASE user_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE order_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO postgres;