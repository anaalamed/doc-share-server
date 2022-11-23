package docSharing.service;

import docSharing.entities.permission.Authorization;
import docSharing.entities.permission.Permission;
import docSharing.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    private PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public void addPermission(int documentId, int userId, Permission permission) {
        Authorization authorization = new Authorization(documentId, userId, permission);
        permissionRepository.save(authorization);
    }

    public void deletePermission(int documentId, int userId) {
        List<Authorization> authorizations = permissionRepository.findByDocIdAndUserId(documentId, userId);
        if (!authorizations.isEmpty()) {
            permissionRepository.delete(authorizations.get(0));
        }
    }

    public void updatePermission(int documentId, int userId, Permission permission) {
        List<Authorization> authorizations = permissionRepository.findByDocIdAndUserId(documentId, userId);
        if (!authorizations.isEmpty()) {
            authorizations.get(0).setPermission(permission);
            permissionRepository.save(authorizations.get(0));
        } else {
            addPermission(documentId, userId, permission);
        }
    }

    public void deleteAuthorizationsForDocument(int documentId) {
        permissionRepository.deleteByDocumentId(documentId);
    }

    public void deleteAuthorizationsForUser(int userId) {
        permissionRepository.deleteByUserId(userId);
    }

    public boolean isAuthorized(int documentId, int userId, Permission permission) {
        List<Authorization> authorizations = permissionRepository.findByDocIdAndUserId(documentId, userId);
        if (authorizations.isEmpty()) {
            return false;
        }

        return (authorizations.get(0).getPermission().compareTo(permission) <= 0);
    }
}
