package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.CreatePermissionRequest;
import edu.usco.campusbookings.application.dto.response.PermissionResponse;
import edu.usco.campusbookings.domain.model.Permission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionMapper {

    public Permission toEntity(CreatePermissionRequest request) {
        return Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .resource(request.getResource())
                .action(request.getAction())
                .build();
    }

    public PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .createdAt(permission.getCreatedDate())
                .updatedAt(permission.getModifiedDate())
                .build();
    }

    public List<PermissionResponse> toResponseList(List<Permission> permissions) {
        return permissions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
