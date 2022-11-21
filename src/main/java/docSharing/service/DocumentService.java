package docSharing.service;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.Folder;
import docSharing.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    private DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public boolean join(int id, User user) {
        Document document = documentRepository.getReferenceById(id);
        document.addActiveUser(user);
        Document savedDocument = documentRepository.save(document);

        return savedDocument.isActiveUser(user);
    }

    public boolean leave(int id, User user) {
        Document document = documentRepository.getReferenceById(id);
        document.removeActiveUser(user);
        Document savedDocument = documentRepository.save(document);

        return !savedDocument.isActiveUser(user);
    }

    public Document update(int id, UpdateRequest updateRequest) {
        Document document = documentRepository.getReferenceById(id);
        document.updateContent(updateRequest);

        return documentRepository.save(document);
    }

    public Document createDocument(User owner, Folder parent, String title) {
        Document document = new Document(owner, parent, title);
        return documentRepository.save(document);
    }

    public boolean delete(int id, User user) {
        Document document = documentRepository.getReferenceById(id);
        if (!(document.hasPermission(user, Permission.OWNER) || document.hasPermission(user, Permission.EDITOR))) {
            return false;
        }

        try {
            documentRepository.delete(document);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean updatePermission(int id, User owner, User user, Permission permission) {
        Document document = documentRepository.getReferenceById(id);
        if (!document.hasPermission(owner, Permission.OWNER)) {
            return false;
        }

        document.updatePermission(user, permission);
        Document savedDocument = documentRepository.save(document);

        return savedDocument.hasPermission(user, permission);
    }
}
