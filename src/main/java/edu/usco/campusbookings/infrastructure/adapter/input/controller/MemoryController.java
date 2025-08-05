package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para monitoreo de memoria del sistema
 * Útil para verificar las optimizaciones aplicadas
 */
@RestController
@RequestMapping("/api/system")
public class MemoryController {

    @GetMapping("/memory")
    public ResponseEntity<Map<String, Object>> getMemoryInfo() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        Runtime runtime = Runtime.getRuntime();
        
        Map<String, Object> memoryInfo = new HashMap<>();
        
        // Información de Heap (memoria principal)
        Map<String, String> heap = new HashMap<>();
        heap.put("used", formatBytes(heapUsage.getUsed()));
        heap.put("committed", formatBytes(heapUsage.getCommitted()));
        heap.put("max", formatBytes(heapUsage.getMax()));
        heap.put("init", formatBytes(heapUsage.getInit()));
        heap.put("usage_percentage", String.format("%.2f%%", 
            (heapUsage.getUsed() * 100.0) / heapUsage.getMax()));
        
        // Información de Non-Heap (metaspace, etc.)
        Map<String, String> nonHeap = new HashMap<>();
        nonHeap.put("used", formatBytes(nonHeapUsage.getUsed()));
        nonHeap.put("committed", formatBytes(nonHeapUsage.getCommitted()));
        nonHeap.put("max", nonHeapUsage.getMax() == -1 ? "Unlimited" : formatBytes(nonHeapUsage.getMax()));
        nonHeap.put("init", formatBytes(nonHeapUsage.getInit()));
        
        // Información del Runtime
        Map<String, String> runtimeInfo = new HashMap<>();
        runtimeInfo.put("total_memory", formatBytes(runtime.totalMemory()));
        runtimeInfo.put("free_memory", formatBytes(runtime.freeMemory()));
        runtimeInfo.put("max_memory", formatBytes(runtime.maxMemory()));
        runtimeInfo.put("used_memory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        
        // Sistema
        Map<String, Object> system = new HashMap<>();
        system.put("available_processors", runtime.availableProcessors());
        system.put("java_version", System.getProperty("java.version"));
        system.put("active_profile", System.getProperty("spring.profiles.active", "default"));
        
        memoryInfo.put("timestamp", System.currentTimeMillis());
        memoryInfo.put("heap", heap);
        memoryInfo.put("non_heap", nonHeap);
        memoryInfo.put("runtime", runtimeInfo);
        memoryInfo.put("system", system);
        
        return ResponseEntity.ok(memoryInfo);
    }
    
    @GetMapping("/memory/summary")
    public ResponseEntity<Map<String, String>> getMemorySummary() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        
        Map<String, String> summary = new HashMap<>();
        summary.put("status", usedMemory < (maxMemory * 0.8) ? "HEALTHY" : "WARNING");
        summary.put("used", formatBytes(usedMemory));
        summary.put("max", formatBytes(maxMemory));
        summary.put("usage_percentage", String.format("%.1f%%", (usedMemory * 100.0) / maxMemory));
        summary.put("optimization_level", getOptimizationLevel(maxMemory));
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/gc")
    public ResponseEntity<String> forceGarbageCollection() {
        long beforeGC = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        System.gc();
        
        // Esperar un poco para que termine el GC
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long afterGC = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long freedMemory = beforeGC - afterGC;
        
        return ResponseEntity.ok(String.format(
            "GC ejecutado. Memoria liberada: %s (antes: %s, después: %s)",
            formatBytes(freedMemory),
            formatBytes(beforeGC),
            formatBytes(afterGC)
        ));
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private String getOptimizationLevel(long maxMemory) {
        if (maxMemory <= 256 * 1024 * 1024) return "ULTRA_OPTIMIZED";
        if (maxMemory <= 512 * 1024 * 1024) return "OPTIMIZED";
        if (maxMemory <= 768 * 1024 * 1024) return "MODERATE";
        return "DEFAULT";
    }
}