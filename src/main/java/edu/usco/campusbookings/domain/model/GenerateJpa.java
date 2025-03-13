package edu.usco.campusbookings.domain.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateJpa {

    private static final String BASE_PACKAGE = "edu.usco.campusbookings.domain.model";
    private static final String DIRECTORY_PATH = "src/main/java/edu/usco/campusbookings/domain/model/";

    private static final String AUDITABLE_CLASS = "package " + BASE_PACKAGE + ";\n" +
            """
        
        import jakarta.persistence.*;
        import lombok.Getter;
        import lombok.Setter;
        import org.springframework.data.jpa.domain.support.AuditingEntityListener;
        import java.time.LocalDateTime;

        @MappedSuperclass
        @EntityListeners(AuditingEntityListener.class)
        @Getter
        @Setter
        public abstract class Auditable {
        
            @CreatedDate
            @Column(name = "created_date", nullable = false, updatable = false)
            private LocalDateTime createdDate;
        
            @LastModifiedDate
            @Column(name = "modified_date")
            private LocalDateTime modifiedDate;
        }
        """;

    private static final String[] ENTITIES = {
            "Usuario", "Rol", "Escenario", "Reserva", "HistorialReserva",
            "Notificacion", "ConfirmacionCorreo", "FormularioReserva", "CalendarioReservas",
            "Feedback"
    };

    private static final String ENTITY_TEMPLATE = "package " + BASE_PACKAGE + ";\n" +
            """
                    
                    import jakarta.persistence.*;
                    import lombok.*;
                    import org.springframework.data.jpa.domain.support.AuditingEntityListener;
                    
                    @EqualsAndHashCode(callSuper = true)
                    @Data
                    @NoArgsConstructor
                    @AllArgsConstructor
                    @Builder
                    @Entity
                    @EntityListeners(AuditingEntityListener.class)
                    public class {CLASS_NAME} extends Auditable {
                    
                        @Id
                        @GeneratedValue(strategy = GenerationType.IDENTITY)
                        private Long id;
                    
                        // Agrega más atributos según sea necesario
                    }
                    """;

    public static void main(String[] args) {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            directory.mkdirs(); // Crear directorios si no existen
        }

        // Crear la clase Auditable
        crearArchivo("Auditable", AUDITABLE_CLASS);

        // Crear todas las entidades
        for (String entity : ENTITIES) {
            crearArchivo(entity, ENTITY_TEMPLATE.replace("{CLASS_NAME}", entity));
        }

        System.out.println("¡Todas las entidades han sido generadas correctamente!");
    }

    private static void crearArchivo(String nombreClase, String contenido) {
        File archivo = new File(DIRECTORY_PATH + nombreClase + ".java");

        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write(contenido);
            System.out.println("Archivo generado: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al crear el archivo " + nombreClase + ": " + e.getMessage());
        }
    }
}
