package edu.usco.campusbookings.domain.model.constants;

/**
 * Constantes de colores oficiales de la Universidad Surcolombiana (USCO)
 * Basado en: https://www.usco.edu.co/imagen-institucional/
 */
public final class USCOColors {

    private USCOColors() {
        // Clase utilitaria - constructor privado
    }

    // ============ COLORES PRINCIPALES ============
    
    /**
     * Vino Tinto - Color principal USCO
     */
    public static final String VINO_TINTO = "#8F141B";
    public static final String VINO_TINTO_LIGHT = "#B15B60";
    public static final String VINO_TINTO_LIGHTER = "#E3C5C6";
    public static final String VINO_TINTO_LIGHTEST = "#DDB8BA";
    public static final String VINO_TINTO_PALE = "#F4E7E8";
    public static final String VINO_TINTO_DARK = "#5C0E12";

    /**
     * Gris - Color secundario USCO
     */
    public static final String GRIS = "#4D626C";
    public static final String GRIS_LIGHT = "#839198";
    public static final String GRIS_LIGHTER = "#A6B1B6";
    public static final String GRIS_LIGHTEST = "#DBE0E2";
    public static final String GRIS_PALE = "#EDEFF0";
    public static final String GRIS_DARK = "#1E262B";

    /**
     * Ocre - Color de acento USCO
     */
    public static final String OCRE = "#DFD4A6";
    public static final String OCRE_LIGHT = "#E5DDB8";
    public static final String OCRE_LIGHTER = "#EFEAD3";
    public static final String OCRE_LIGHTEST = "#F5F2E4";
    public static final String OCRE_PALE = "#F9F6ED";
    public static final String OCRE_DARK = "#C7B363";

    // ============ COLORES POR FACULTADES ============
    
    /**
     * Facultad de Ciencias Jurídicas y Políticas
     */
    public static final String FACULTAD_JURIDICAS = "#7C0B69";

    /**
     * Facultad de Ciencias Exactas y Naturales
     */
    public static final String FACULTAD_EXACTAS = "#9DC107";

    /**
     * Facultad de Ciencias Sociales y Humanas
     */
    public static final String FACULTAD_SOCIALES = "#CE932C";

    /**
     * Facultad de Economía y Administración
     */
    public static final String FACULTAD_ECONOMIA = "#003561";

    /**
     * Facultad de Educación
     */
    public static final String FACULTAD_EDUCACION = "#AD142E";

    /**
     * Facultad de Ingeniería
     */
    public static final String FACULTAD_INGENIERIA = "#7D9C10";

    /**
     * Facultad de Salud
     */
    public static final String FACULTAD_SALUD = "#00A4B7";

    // ============ ALIASES PARA USO COMÚN ============
    
    /**
     * Color primario del sistema
     */
    public static final String PRIMARY = VINO_TINTO;
    public static final String PRIMARY_LIGHT = VINO_TINTO_LIGHT;
    public static final String PRIMARY_DARK = VINO_TINTO_DARK;

    /**
     * Color secundario del sistema
     */
    public static final String SECONDARY = GRIS;
    public static final String SECONDARY_LIGHT = GRIS_LIGHT;
    public static final String SECONDARY_DARK = GRIS_DARK;

    /**
     * Color de acento del sistema
     */
    public static final String ACCENT = OCRE;
    public static final String ACCENT_LIGHT = OCRE_LIGHT;
    public static final String ACCENT_DARK = OCRE_DARK;

    // ============ COLORES FUNCIONALES ============
    
    /**
     * Colores para diferentes estados y propósitos
     */
    public static final String SUCCESS = FACULTAD_EXACTAS;
    public static final String WARNING = OCRE;
    public static final String DANGER = VINO_TINTO;
    public static final String INFO = FACULTAD_SALUD;

    /**
     * Colores para texto y fondos
     */
    public static final String BACKGROUND = GRIS_PALE;
    public static final String TEXT_PRIMARY = GRIS_DARK;
    public static final String TEXT_SECONDARY = GRIS;
    public static final String BORDER = GRIS_LIGHTER;

    // ============ MÉTODOS UTILITARIOS ============

    /**
     * Convierte un color hexadecimal a RGB
     * @param hex Color en formato hexadecimal (ej: "#8F141B")
     * @return Array con valores RGB [r, g, b]
     */
    public static int[] hexToRgb(String hex) {
        if (hex == null || hex.length() != 7 || !hex.startsWith("#")) {
            throw new IllegalArgumentException("Formato de color inválido. Use #RRGGBB");
        }
        
        return new int[]{
            Integer.parseInt(hex.substring(1, 3), 16), // R
            Integer.parseInt(hex.substring(3, 5), 16), // G
            Integer.parseInt(hex.substring(5, 7), 16)  // B
        };
    }

    /**
     * Convierte valores RGB a hexadecimal
     * @param r Valor rojo (0-255)
     * @param g Valor verde (0-255)
     * @param b Valor azul (0-255)
     * @return Color en formato hexadecimal
     */
    public static String rgbToHex(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Los valores RGB deben estar entre 0 y 255");
        }
        
        return String.format("#%02X%02X%02X", r, g, b);
    }

    /**
     * Obtiene el color de una facultad por su nombre
     * @param facultad Nombre de la facultad
     * @return Color hexadecimal de la facultad
     */
    public static String getColorFacultad(String facultad) {
        if (facultad == null) {
            return PRIMARY;
        }
        
        String facultadLower = facultad.toLowerCase();
        
        if (facultadLower.contains("juridica") || facultadLower.contains("politica")) {
            return FACULTAD_JURIDICAS;
        } else if (facultadLower.contains("exactas") || facultadLower.contains("naturales")) {
            return FACULTAD_EXACTAS;
        } else if (facultadLower.contains("sociales") || facultadLower.contains("humanas")) {
            return FACULTAD_SOCIALES;
        } else if (facultadLower.contains("economia") || facultadLower.contains("administracion")) {
            return FACULTAD_ECONOMIA;
        } else if (facultadLower.contains("educacion")) {
            return FACULTAD_EDUCACION;
        } else if (facultadLower.contains("ingenieria")) {
            return FACULTAD_INGENIERIA;
        } else if (facultadLower.contains("salud")) {
            return FACULTAD_SALUD;
        } else {
            return PRIMARY; // Color por defecto
        }
    }

    /**
     * Verifica si un color está dentro de la paleta oficial USCO
     * @param color Color en formato hexadecimal
     * @return true si es un color oficial USCO
     */
    public static boolean isUSCOColor(String color) {
        if (color == null) {
            return false;
        }
        
        String colorUpper = color.toUpperCase();
        
        return colorUpper.equals(VINO_TINTO.toUpperCase()) ||
               colorUpper.equals(VINO_TINTO_LIGHT.toUpperCase()) ||
               colorUpper.equals(GRIS.toUpperCase()) ||
               colorUpper.equals(GRIS_LIGHT.toUpperCase()) ||
               colorUpper.equals(OCRE.toUpperCase()) ||
               colorUpper.equals(OCRE_LIGHT.toUpperCase()) ||
               colorUpper.equals(FACULTAD_JURIDICAS.toUpperCase()) ||
               colorUpper.equals(FACULTAD_EXACTAS.toUpperCase()) ||
               colorUpper.equals(FACULTAD_SOCIALES.toUpperCase()) ||
               colorUpper.equals(FACULTAD_ECONOMIA.toUpperCase()) ||
               colorUpper.equals(FACULTAD_EDUCACION.toUpperCase()) ||
               colorUpper.equals(FACULTAD_INGENIERIA.toUpperCase()) ||
               colorUpper.equals(FACULTAD_SALUD.toUpperCase());
    }
}