package edu.usco.campusbookings.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ConditionalOnProperty(name = "DATABASE_URL")
public class RailwayDataSourceConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        
        // Railway proporciona DATABASE_URL en formato: postgresql://user:pass@host:port/db
        // Convertir a formato JDBC: jdbc:postgresql://host:port/db
        String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");
        dataSource.setJdbcUrl(jdbcUrl);
        
        // Configuración de Hikari optimizada para Railway
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        
        return dataSource;
    }
} 