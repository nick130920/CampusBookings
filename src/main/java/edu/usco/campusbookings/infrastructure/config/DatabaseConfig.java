package edu.usco.campusbookings.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${PGHOST:localhost}")
    private String dbHost;

    @Value("${PGPORT:5432}")
    private String dbPort;

    @Value("${PGDATABASE:CampusBookings}")
    private String dbName;

    @Value("${PGUSER:postgres}")
    private String dbUser;

    @Value("${PGPASSWORD:secret}")
    private String dbPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        
        // Si DATABASE_URL está presente (Railway), usarla directamente
        if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("postgresql://")) {
            // Railway proporciona DATABASE_URL en formato: postgresql://user:pass@host:port/db
            // Convertir a formato JDBC: jdbc:postgresql://host:port/db
            String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");
            dataSource.setJdbcUrl(jdbcUrl);
        } else {
            // Fallback a configuración manual (desarrollo local)
            dataSource.setJdbcUrl("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName);
            dataSource.setUsername(dbUser);
            dataSource.setPassword(dbPassword);
        }
        
        // Configuración de Hikari
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        
        return dataSource;
    }
} 