package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionsResponse {
    private Long userId;
    private String roleName;
    private List<PermissionDto> permissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionDto {
        private Long id;
        private String name;
        private String description;
        private String resource;
        private String action;
    }
}
