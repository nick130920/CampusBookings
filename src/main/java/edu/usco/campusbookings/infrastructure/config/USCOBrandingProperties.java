package edu.usco.campusbookings.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Propiedades de configuración para el branding USCO
 * Lee la configuración desde application.properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "usco.branding")
public class USCOBrandingProperties {

    private Institution institution = new Institution();
    private Colors colors = new Colors();
    private Typography typography = new Typography();
    private Assets assets = new Assets();
    private Theme theme = new Theme();

    @Data
    public static class Institution {
        private String name = "Universidad Surcolombiana";
        private String shortName = "USCO";
        private String website = "https://www.usco.edu.co/";
        private String brandGuide = "https://www.usco.edu.co/imagen-institucional/";
    }

    @Data
    public static class Colors {
        private String primary = "#8F141B";
        private String primaryLight = "#B15B60";
        private String primaryDark = "#5C0E12";
        private String secondary = "#4D626C";
        private String secondaryLight = "#839198";
        private String secondaryDark = "#1E262B";
        private String accent = "#DFD4A6";
        private String accentLight = "#E5DDB8";
        private String accentDark = "#C7B363";
        
        private Facultad facultad = new Facultad();

        @Data
        public static class Facultad {
            private String juridicas = "#7C0B69";
            private String exactas = "#9DC107";
            private String sociales = "#CE932C";
            private String economia = "#003561";
            private String educacion = "#AD142E";
            private String ingenieria = "#7D9C10";
            private String salud = "#00A4B7";
        }
    }

    @Data
    public static class Typography {
        private String fontFamily = "Open Sans";
        private String fontFamilyCondensed = "Open Sans Condensed";
        private String googleFonts = "https://fonts.googleapis.com/css?family=Open+Sans:400,400i,700,700i";
        private String googleFontsCondensed = "https://fonts.googleapis.com/css?family=Open+Sans+Condensed:300,300i,700";
    }

    @Data
    public static class Assets {
        private Logo logo = new Logo();
        private String favicon = "favicon.ico";

        @Data
        public static class Logo {
            private String basePath = "/static/images/logos/";
            private Horizontal horizontal = new Horizontal();
            private Vertical vertical = new Vertical();

            @Data
            public static class Horizontal {
                private String base = "universidad-surcolombiana.png";
                private String medium = "universidad-surcolombiana-m.png";
                private String small = "universidad-surcolombiana-p.png";
            }

            @Data
            public static class Vertical {
                private String base = "universidad-surcolombiana-v.png";
                private String medium = "universidad-surcolombiana-vm.png";
                private String small = "universidad-surcolombiana-vp.png";
            }
        }
    }

    @Data
    public static class Theme {
        private boolean enableCustomCss = true;
        private String cssPath = "/static/css/usco-colors.css";
        private String jsPath = "/static/js/usco-theme.js";
    }

    /**
     * Obtiene el color de una facultad por su nombre
     */
    public String getFacultadColor(String facultadNombre) {
        if (facultadNombre == null) {
            return colors.primary;
        }
        
        String facultadLower = facultadNombre.toLowerCase();
        
        if (facultadLower.contains("juridica") || facultadLower.contains("politica")) {
            return colors.facultad.juridicas;
        } else if (facultadLower.contains("exactas") || facultadLower.contains("naturales")) {
            return colors.facultad.exactas;
        } else if (facultadLower.contains("sociales") || facultadLower.contains("humanas")) {
            return colors.facultad.sociales;
        } else if (facultadLower.contains("economia") || facultadLower.contains("administracion")) {
            return colors.facultad.economia;
        } else if (facultadLower.contains("educacion")) {
            return colors.facultad.educacion;
        } else if (facultadLower.contains("ingenieria")) {
            return colors.facultad.ingenieria;
        } else if (facultadLower.contains("salud")) {
            return colors.facultad.salud;
        } else {
            return colors.primary;
        }
    }

    /**
     * Obtiene la URL completa de un logo
     */
    public String getLogoUrl(String tipo, String tamaño) {
        String basePath = assets.logo.basePath;
        
        if ("horizontal".equalsIgnoreCase(tipo)) {
            switch (tamaño.toLowerCase()) {
                case "medium":
                case "m":
                    return basePath + assets.logo.horizontal.medium;
                case "small":
                case "p":
                    return basePath + assets.logo.horizontal.small;
                default:
                    return basePath + assets.logo.horizontal.base;
            }
        } else if ("vertical".equalsIgnoreCase(tipo)) {
            switch (tamaño.toLowerCase()) {
                case "medium":
                case "m":
                    return basePath + assets.logo.vertical.medium;
                case "small":
                case "p":
                    return basePath + assets.logo.vertical.small;
                default:
                    return basePath + assets.logo.vertical.base;
            }
        }
        
        return basePath + assets.logo.horizontal.base;
    }

    /**
     * Obtiene todos los colores como un mapa
     */
    public Map<String, Object> getAllColorsAsMap() {
        return Map.of(
            "primary", colors.primary,
            "primaryLight", colors.primaryLight,
            "primaryDark", colors.primaryDark,
            "secondary", colors.secondary,
            "secondaryLight", colors.secondaryLight,
            "secondaryDark", colors.secondaryDark,
            "accent", colors.accent,
            "accentLight", colors.accentLight,
            "accentDark", colors.accentDark,
            "facultades", Map.of(
                "juridicas", colors.facultad.juridicas,
                "exactas", colors.facultad.exactas,
                "sociales", colors.facultad.sociales,
                "economia", colors.facultad.economia,
                "educacion", colors.facultad.educacion,
                "ingenieria", colors.facultad.ingenieria,
                "salud", colors.facultad.salud
            )
        );
    }
}