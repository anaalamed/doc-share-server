package docSharing.service;

import docSharing.utils.Utils;
import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;
import docSharing.entities.file.Document;
import docSharing.entities.file.Folder;
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

    static Map<Integer,String> documentsContentCache = new HashMap<>();
    static Map<Integer,String> documentsContentDBCache = new HashMap<>();

    private DocumentService(DocumentRepository documentRepository, FolderRepository folderRepository,
                            UserRepository userRepository, PermissionRepository permissionRepository) {
        this.documentRepository = documentRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Adds userId to the document's activeUsers list.
     * @param id
     * @param userId
     */
    public void join(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.addActiveUser(userId);
    }

    /**
     * Removes userId from the document's activeUsers list.
     * @param id
     * @param userId
     */
    public void leave(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        document.removeActiveUser(userId);
    }

    /**
     * Creates a Document and saves it to the database.
     * @param ownerId
     * @param parentId
     * @param title
     * @return The new document
     */
    public Document createDocument(int ownerId, int parentId, String title) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (!owner.isPresent()) {
            throw new IllegalArgumentException(String.format("owner ID: %d was not found!", ownerId));
        }

        Optional<Folder> parent = folderRepository.findById(parentId);
        Utils.validateTitle(parent, title);

        Document document = new Document(owner.get(), parentId, title);
        updateContentOnCache(document.getId(), document.getContent());
        Document savedDocument = documentRepository.save(document);
        addDocumentToParentSubFiles(document);

        return savedDocument;
    }

    /**
     * Updates a document and adds a track log to its history.
     * @param documentId
     * @param updateRequest
     */
    public void update(int documentId, UpdateRequest updateRequest) {
        Document document = documentRepository.getReferenceById(documentId);
        document.updateContent(updateRequest);

        updateContentOnCache(documentId, document.getContent());
    }

    /**
     * Updates document's parent folder.
     * @param id
     * @param parentId
     * @return
     */
    public Document setParent(int id, int parentId) {
        Document document = documentRepository.getReferenceById(id);
        Optional<Folder> parentToBe = folderRepository.findById(parentId);

        Utils.validateTitle(parentToBe, document.getMetadata().getTitle());

        removeDocumentFromParentSubFiles(document);
        document.getMetadata().setParentId(parentId);
        Document savedDocument = documentRepository.save(document);
        addDocumentToParentSubFiles(document);

        return savedDocument;
    }

    /**
     * Updates document's title.
     * @param id
     * @param title
     * @return
     */
    public Document setTitle(int id, String title) {
        Document document = documentRepository.getReferenceById(id);
        Optional<Folder> parent = folderRepository.findById(document.getMetadata().getParentId());

        Utils.validateTitle(parent, title);
        document.setTitle(title);

        return documentRepository.save(document);
    }

    /**
     * Sends a share notification email to the specified email address.
     * @param documentId
     * @param email
     * @param permission
     * @return Success status
     */
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

    /**
     * @param documentId
     * @return The document's URL
     */
    public String getUrl(int documentId) {
        Document document = documentRepository.getReferenceById(documentId);
        return generateUrl(documentId);
    }

    /**
     * Imports a document from specified filepath.
     * Will create a new document with the imported title and content.
     * @param path
     * @param ownerId
     * @param parentID
     * @return The imported document
     */
    public Document importFile(String path, int ownerId, int parentID){
        Document document = createDocument(ownerId,parentID, getFileName(path));
        updateContent(document.getId(), readFromFile(path));

        return document;
    }

    /**
     * Exports a document to a text file.
     * @param documentId
     */
    public void exportFile(int documentId){
        Document document = documentRepository.getReferenceById(documentId);

        String filename = document.getMetadata().getTitle();
        String content = document.getContent();
        String home = System.getProperty("user.home");
        String filePath = home + "\\Downloads\\" + filename + ".txt";

        writeToFile(content, filePath);
    }

    /**
     * Deletes document from the database.
     * @param documentId
     * @return
     */
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

    private void updateContentOnCache(int documentId, String content){
        documentsContentCache.put(documentId,content);
    }

    private void deleteFromCache(int documentID){
        documentsContentCache.remove(documentID);
        documentsContentDBCache.remove(documentID);
    }

    private void updateContentOnDB(){
        documentsContentCache.forEach((key, value)->{
            if (!documentsContentDBCache.containsKey(key) || !value.equals(documentsContentDBCache.get(key))) {
                updateContent(key, value);
            }
        });
    }

    private void updateContent(int documentId, String content){
        Document updatedDocument = documentRepository.getReferenceById(documentId);
        updatedDocument.setContent(content);
        documentRepository.save(updatedDocument);
        updateContentOnCache(documentId, content);
    }

    private String generateUrl(int documentId) {
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
}