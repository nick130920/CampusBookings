package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.domain.model.constants.USCOColors;
import edu.usco.campusbookings.infrastructure.config.USCOBrandingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para obtener información de branding y colores institucionales USCO
 */
@RestController
@RequestMapping("/api/branding")
@RequiredArgsConstructor
public class USCOBrandingController {

    private final USCOBrandingProperties brandingProperties;

    @GetMapping("/colors")
    public ResponseEntity<Map<String, Object>> getUSCOColors() {
        Map<String, Object> response = new HashMap<>();
        
        // Usar las propiedades configurables
        response.putAll(brandingProperties.getAllColorsAsMap());
        response.put("fuente", brandingProperties.getInstitution().getBrandGuide());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/colors/facultad/{nombre}")
    public ResponseEntity<Map<String, String>> getColorFacultad(@PathVariable String nombre) {
        String color = brandingProperties.getFacultadColor(nombre);
        
        Map<String, String> response = new HashMap<>();
        response.put("facultad", nombre);
        response.put("color", color);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/colors/validate")
    public ResponseEntity<Map<String, Object>> validateUSCOColor(@RequestBody Map<String, String> request) {
        String color = request.get("color");
        
        if (color == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("message", "El color es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }
        
        boolean isValid = USCOColors.isUSCOColor(color);
        
        Map<String, Object> response = new HashMap<>();
        response.put("color", color);
        response.put("valid", isValid);
        response.put("message", isValid ? "Color oficial USCO" : "Color no pertenece a la paleta oficial USCO");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/colors/convert")
    public ResponseEntity<Map<String, Object>> convertColor(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (request.containsKey("hex")) {
                String hex = (String) request.get("hex");
                int[] rgb = USCOColors.hexToRgb(hex);
                
                response.put("hex", hex);
                response.put("rgb", Map.of("r", rgb[0], "g", rgb[1], "b", rgb[2]));
                response.put("rgbString", String.format("rgb(%d, %d, %d)", rgb[0], rgb[1], rgb[2]));
                
            } else if (request.containsKey("r") && request.containsKey("g") && request.containsKey("b")) {
                int r = ((Number) request.get("r")).intValue();
                int g = ((Number) request.get("g")).intValue();
                int b = ((Number) request.get("b")).intValue();
                String hex = USCOColors.rgbToHex(r, g, b);
                
                response.put("rgb", Map.of("r", r, "g", g, "b", b));
                response.put("hex", hex);
                response.put("rgbString", String.format("rgb(%d, %d, %d)", r, g, b));
            } else {
                response.put("error", "Proporcione 'hex' o valores 'r', 'g', 'b'");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getBrandingInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // Información institucional
        info.put("universidad", brandingProperties.getInstitution().getName());
        info.put("sigla", brandingProperties.getInstitution().getShortName());
        info.put("sitioWeb", brandingProperties.getInstitution().getWebsite());
        info.put("guiaImagenInstitucional", brandingProperties.getInstitution().getBrandGuide());
        
        // Información de tipografía
        Map<String, Object> tipografia = new HashMap<>();
        tipografia.put("principal", brandingProperties.getTypography().getFontFamily());
        tipografia.put("googleFonts", brandingProperties.getTypography().getGoogleFonts());
        tipografia.put("alternativa", brandingProperties.getTypography().getFontFamilyCondensed());
        tipografia.put("googleFontsCondensed", brandingProperties.getTypography().getGoogleFontsCondensed());
        
        // Información de logos
        Map<String, Object> logos = new HashMap<>();
        logos.put("basePath", brandingProperties.getAssets().getLogo().getBasePath());
        logos.put("horizontal", Map.of(
            "base", brandingProperties.getAssets().getLogo().getHorizontal().getBase(),
            "mediano", brandingProperties.getAssets().getLogo().getHorizontal().getMedium(),
            "pequeño", brandingProperties.getAssets().getLogo().getHorizontal().getSmall()
        ));
        logos.put("vertical", Map.of(
            "base", brandingProperties.getAssets().getLogo().getVertical().getBase(),
            "mediano", brandingProperties.getAssets().getLogo().getVertical().getMedium(),
            "pequeño", brandingProperties.getAssets().getLogo().getVertical().getSmall()
        ));
        logos.put("variantes", new String[]{"base", "ocre", "negro", "blanco"});
        
        // Configuración de tema
        Map<String, Object> tema = new HashMap<>();
        tema.put("cssHabilitado", brandingProperties.getTheme().isEnableCustomCss());
        tema.put("cssPath", brandingProperties.getTheme().getCssPath());
        tema.put("jsPath", brandingProperties.getTheme().getJsPath());
        
        info.put("tipografia", tipografia);
        info.put("logos", logos);
        info.put("tema", tema);
        info.put("favicon", brandingProperties.getAssets().getFavicon());
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/logos/{tipo}")
    public ResponseEntity<Map<String, String>> getLogo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "base") String tamaño) {
        
        String logoUrl = brandingProperties.getLogoUrl(tipo, tamaño);
        
        Map<String, String> response = new HashMap<>();
        response.put("tipo", tipo);
        response.put("tamaño", tamaño);
        response.put("url", logoUrl);
        
        return ResponseEntity.ok(response);
    }
}