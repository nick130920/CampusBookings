package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Controlador para diagnósticos de zona horaria y fechas
 */
@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    @GetMapping("/timezone")
    public ResponseEntity<Map<String, Object>> getTimezoneInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // Información de zona horaria del sistema
        info.put("systemTimezone", TimeZone.getDefault().getID());
        info.put("systemTimezoneDisplayName", TimeZone.getDefault().getDisplayName());
        info.put("systemTimezoneOffset", TimeZone.getDefault().getRawOffset() / (1000 * 60 * 60));
        
        // Información de ZoneId
        ZoneId defaultZone = ZoneId.systemDefault();
        info.put("zoneId", defaultZone.getId());
        
        // Fechas actuales en diferentes formatos
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.now();
        ZonedDateTime bogotaTime = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        
        info.put("localDateTime", now);
        info.put("zonedDateTime", zonedNow);
        info.put("bogotaTime", bogotaTime);
        info.put("utcTime", ZonedDateTime.now(ZoneId.of("UTC")));
        
        // Información del sistema operativo
        info.put("osTimezone", System.getProperty("user.timezone"));
        info.put("jvmTimezone", System.getProperty("user.timezone"));
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/test-audit")
    public ResponseEntity<Map<String, Object>> testAuditDates() {
        Map<String, Object> info = new HashMap<>();
        
        // Simular lo que haría JPA Auditing
        LocalDateTime auditDate = LocalDateTime.now();
        info.put("currentAuditTime", auditDate);
        info.put("auditTimeFormatted", auditDate.toString());
        
        // Información de configuración
        info.put("message", "Esta fecha simula lo que JPA Auditing guardaría en createdDate/modifiedDate");
        
        return ResponseEntity.ok(info);
    }
}