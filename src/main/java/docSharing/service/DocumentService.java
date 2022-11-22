package docSharing.service;

import docSharing.controller.request.ShareRequest;
import docSharing.controller.request.UpdateRequest;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.File;
import docSharing.entities.document.Folder;
import docSharing.repository.DocumentRepository;
import docSharing.repository.FolderRepository;
import docSharing.utils.GMailer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;

    private DocumentService(DocumentRepository documentRepository, FolderRepository folderRepository) {
        this.documentRepository = documentRepository;
        this.folderRepository = folderRepository;
    }

    public boolean join(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.addActiveUser(userId);
        Document savedDocument = documentRepository.save(document);

        return savedDocument.isActiveUser(userId);
    }

    public boolean leave(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.removeActiveUser(userId);
        Document savedDocument = documentRepository.save(document);

        return !savedDocument.isActiveUser(userId);
    }

    public Document update(int id, UpdateRequest updateRequest) {
        Document document = documentRepository.getReferenceById(id);
        document.updateContent(updateRequest);

        return documentRepository.save(document);
    }

    public Document createDocument(int ownerId, int parentId, String title) {
        Document document = new Document(ownerId, parentId, title);
        return documentRepository.save(document);
    }

    public boolean delete(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        if (!(document.hasPermission(userId, Permission.OWNER) || document.hasPermission(userId, Permission.EDITOR))) {
            return false;
        }

        try {
            documentRepository.delete(document);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void setParent(int id, int parentId, int userId) {
        Document document = documentRepository.getReferenceById(id);
        if (!(document.hasPermission(userId, Permission.OWNER) || document.hasPermission(userId, Permission.EDITOR))) {
            throw new IllegalArgumentException(String.format("User: %d does not have permission for this operation!", userId));
        }

        if (parentId > 0 &&
                isTitleExistsInFolder(document.getMetadata().getTitle(), folderRepository.getReferenceById(parentId))) {
            throw new IllegalArgumentException(String.format("File with title: %s already exists in that folder!",
                    document.getMetadata().getTitle()));
        }

        document.setParentId(parentId);
    }

    public void setTitle(int id, String title, int userId) {
        Document document = documentRepository.getReferenceById(id);
        if (!(document.hasPermission(userId, Permission.OWNER) || document.hasPermission(userId, Permission.EDITOR))) {
            throw new IllegalArgumentException(String.format("User: %d does not have permission for this operation!", userId));
        }

        int parentId = document.getMetadata().getParentId();
        if (parentId > 0 &&
                isTitleExistsInFolder(title, folderRepository.getReferenceById(parentId))) {
            throw new IllegalArgumentException(String.format("File with title: %s already exists in that folder!", title));
        }

        document.setTitle(title);
    }

    private boolean isTitleExistsInFolder(String title, Folder folder) {
        for (File file : folder.getSubFiles()) {
            if (file.getMetadata().getTitle().equals(title)) {
                return true;
            }
        }

        return false;
    }

    public boolean updatePermission(int documentId, int ownerId, User user, Permission permission) {
        Optional<Document> document = documentRepository.findById(documentId);

        if (!document.get().hasPermission(ownerId, Permission.OWNER)) {
            return false;
        }

        document.get().updatePermission(user.getId(), permission);
        Document savedDocument = documentRepository.save(document.get());

        return savedDocument.hasPermission(user.getId(), permission);
    }

    public boolean share(@NotNull ShareRequest shareRequest) {
        boolean success = true;

        for (User user : shareRequest.getUsers()) {
            if (!updatePermission(shareRequest.getDocumentID(), shareRequest.getOwnerID(), user,
                    shareRequest.getPermission())) {
                success = false;
                continue;
            }
            if (shareRequest.isNotify()) {
                success = success && notifyShareByEmail(shareRequest.getDocumentID(), user.getEmail(), shareRequest.getPermission());
            }
        }

        return success;
    }

    private boolean notifyShareByEmail(int documentId, String email, Permission permission) {
        Document document = documentRepository.getReferenceById(documentId);

        try {
            String subject = "Document shared with you: " + document.getMetadata().getTitle();
            String message = String.format("The document owner has invited you to %s the following document: %s",
                    permission.toString(), generateUrl(documentId));
            GMailer.sendMail(email, subject, message);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String generateUrl(int id) {
        File file = documentRepository.getReferenceById(id);
        String url = file.getMetadata().getTitle();

        while (file.getMetadata().getParentId() > 0) {
            Folder parent = folderRepository.getReferenceById(file.getMetadata().getParentId());

            url = parent.getMetadata().getTitle() + FileSystems.getDefault().getSeparator() + url;
            file = parent;
        }

        return url;
    }


}