package docSharing.service;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.File;
import docSharing.entities.document.Folder;
import docSharing.repository.DocumentRepository;
import docSharing.repository.FolderRepository;
import docSharing.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    private DocumentService(DocumentRepository documentRepository, FolderRepository folderRepository,
                            UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    public void join(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.addActiveUser(userId);
    }

    public void leave(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.removeActiveUser(userId);
    }

    public Document update(int id, UpdateRequest updateRequest) {
        Document document = documentRepository.getReferenceById(id);
        document.updateContent(updateRequest);

        return documentRepository.save(document);
    }

    public Document createDocument(int ownerId, int parentId, String title) {
        User owner = userRepository.getReferenceById(ownerId);
        Folder parent = folderRepository.getReferenceById(parentId);
        Document document = new Document(owner, parent, title);

        return documentRepository.save(document);
    }

    public boolean delete(int id) {
        Document document = documentRepository.getReferenceById(id);

        try {
            documentRepository.delete(document);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void setParent(int id, int parentId) {
        Document document = documentRepository.getReferenceById(id);
        Folder parentToBe = folderRepository.getReferenceById(parentId);

        if (parentToBe != null &&
                isTitleExistsInFolder(document.getMetadata().getTitle(), parentToBe)) {
            throw new IllegalArgumentException(String.format("File with title: %s already exists in that folder!",
                    document.getMetadata().getTitle()));
        }

        document.getMetadata().setParent(parentToBe);
    }

    public Document setTitle(int id, String title) {
        Document document = documentRepository.getReferenceById(id);

        if (isTitleExistsInFolder(title, document.getMetadata().getParent())) {
            throw new IllegalArgumentException(String.format("File with title: %s already exists in that folder!", title));
        }

        document.setTitle(title);
        return document;
    }

    private boolean isTitleExistsInFolder(String title, Folder folder) {
        for (File file : folder.getSubFiles()) {
            if (file.getMetadata().getTitle().equals(title)) {
                return true;
            }
        }

        return false;
    }

    public String getUrl(int documentId) {
        Document document = documentRepository.getReferenceById(documentId);
        return document.generateUrl();
    }
}