package docSharing.service;

import docSharing.utils.Utils;
import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.Folder;
import docSharing.entities.permission.Permission;
import docSharing.repository.DocumentRepository;
import docSharing.repository.FolderRepository;
import docSharing.repository.PermissionRepository;
import docSharing.repository.UserRepository;
import docSharing.utils.GMailer;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static docSharing.utils.FilesUtils.*;


@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    static Map<Integer,String> documentCacheChanges = new HashMap<>();//doc id, doc content
    static Map<Integer,String> documentCacheDBContent = new HashMap<>();

    private DocumentService(DocumentRepository documentRepository, FolderRepository folderRepository,
                            UserRepository userRepository, PermissionRepository permissionRepository) {
        this.documentRepository = documentRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public void join(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.addActiveUser(userId);
    }

    public void leave(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.removeActiveUser(userId);
    }

    public Document createDocument(int ownerId, int parentId, String title) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (!owner.isPresent()) {
            throw new IllegalArgumentException(String.format("owner ID: %d was not found!", ownerId));
        }

        Optional<Folder> parent = folderRepository.findById(parentId);
        Utils.validateUniqueTitle(parent, title);

        Document document = new Document(owner.get(), parentId, title);
        updateContentOnCache(document.getId(), document.getContent());
        Document savedDocument = documentRepository.save(document);
        addDocumentToParentSubFiles(document);

        return savedDocument;
    }

    public void update(int documentId, UpdateRequest updateRequest) {
        Document document = documentRepository.getReferenceById(documentId);
        document.updateContent(updateRequest);

        updateContentOnCache(documentId, document.getContent());
    }

    private void updateContentOnCache(int documentId, String content){
        documentCacheChanges.put(documentId,content);
    }

    private void deleteFromCache(int documentID){
        documentCacheChanges.remove(documentID);
        documentCacheDBContent.remove(documentID);
    }

    private void updateContentOnDB(){
        documentCacheChanges.forEach((key, value)->{
            if (!documentCacheDBContent.containsKey(key) || !value.equals(documentCacheDBContent.get(key))) {
                updateContent(key, value);
            }
        });
    }

    private void updateContent(int documentId, String content){
        Document updatedDocument = documentRepository.getReferenceById(documentId);
        updatedDocument.setContent(content);
        documentRepository.save(updatedDocument);
        documentCacheDBContent.put(documentId, content);
    }

    public Document setParent(int id, int parentId) {
        Document document = documentRepository.getReferenceById(id);
        Optional<Folder> parentToBe = folderRepository.findById(parentId);

        Utils.validateUniqueTitle(parentToBe, document.getMetadata().getTitle());

        removeDocumentFromParentSubFiles(document);
        document.getMetadata().setParentId(parentId);
        Document savedDocument = documentRepository.save(document);
        addDocumentToParentSubFiles(document);

        return savedDocument;
    }

    public Document setTitle(int id, String title) {
        Document document = documentRepository.getReferenceById(id);
        Optional<Folder> parent = folderRepository.findById(document.getMetadata().getParentId());

        Utils.validateUniqueTitle(parent, title);
        document.setTitle(title);

        return documentRepository.save(document);
    }

    public boolean notifyShareByEmail(int documentId, String email, Permission permission) {
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

    public String getUrl(int documentId) {
        Document document = documentRepository.getReferenceById(documentId);
        return generateUrl(documentId);
    }

    public String generateUrl(int documentId) {
        Document document = documentRepository.getReferenceById(documentId);

        String url = document.getMetadata().getTitle();
        int parentId = document.getMetadata().getParentId();

        while (parentId > 0) {
            Folder parent = folderRepository.getReferenceById(parentId);
            url = parent.getMetadata().getTitle() + FileSystems.getDefault().getSeparator() + url;
            parentId = parent.getMetadata().getParentId();
        }

        return url;
    }

    public boolean delete(int documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (!document.isPresent()) {
            return false;
        }

        try {
            removeDocumentFromParentSubFiles(document.get());
            permissionRepository.deleteByDocumentId(documentId);
            documentRepository.delete(document.get());
            deleteFromCache(document.get().getId());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void addDocumentToParentSubFiles(Document document) {
        Optional<Folder> optionalParent = folderRepository.findById(document.getMetadata().getParentId());

        if (optionalParent.isPresent()) {
            optionalParent.get().addSubFile(document);
            folderRepository.save(optionalParent.get());
        }
    }

    private void removeDocumentFromParentSubFiles(Document document) {
        Optional<Folder> optionalParent = folderRepository.findById(document.getMetadata().getParentId());

        if (optionalParent.isPresent()) {
            optionalParent.get().removeSubFile(document);
            folderRepository.save(optionalParent.get());
        }
    }

    public Document importFile(String path, int ownerId, int parentID){
        Document importDocument=createDocument(ownerId,parentID, getFileName(path));
        importDocument.setContent(readFromFile(path));
        return importDocument;
    }

    public void exportFile(int documentId){
        Document document = documentRepository.getReferenceById(documentId);

        String filename=document.getMetadata().getTitle();
        String content=document.getContent();

        String pathFile = "C:/Users/Downloads/"+filename+".txt";
        writeToFile(content, pathFile);
    }
}