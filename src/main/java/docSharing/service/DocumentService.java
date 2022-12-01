package docSharing.service;

import docSharing.entities.file.File;
import docSharing.entities.file.UpdateLog;
import docSharing.repository.*;
import docSharing.utils.Utils;
import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;
import docSharing.entities.file.Document;
import docSharing.entities.file.Folder;
import docSharing.entities.permission.Permission;
import docSharing.utils.GMailer;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static docSharing.utils.FilesUtils.*;


@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UpdateLogRepository updateLogRepository;
    private Map<Integer, Document> documentsCache;


    private DocumentService(DocumentRepository documentRepository, FolderRepository folderRepository,
                            UserRepository userRepository, PermissionRepository permissionRepository,
                            UpdateLogRepository updateLogRepository) {
        this.documentRepository = documentRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.updateLogRepository = updateLogRepository;

        this.loadDocumentCache();

    }

    private void loadDocumentCache() {
        this.documentsCache = documentRepository.findAll().stream()
                .collect(Collectors.toMap(File::getId, Function.identity()));
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
        Document saved = documentRepository.save(document);
        this.documentsCache.put(document.getId(), saved);
        addDocumentToParentSubFiles(document);

        return this.documentsCache.get(document.getId());
    }

    /**
     * Updates a document and adds a track log to its history.
     * @param documentId
     * @param updateRequest
     */
    public void update(int documentId, UpdateRequest updateRequest) {
        Document document = documentsCache.get(documentId);
        UpdateLog updateLog = document.updateContent(updateRequest);

        if (document.isContinuousLog(updateLog)) {
            document.updateLastLog(updateLog);
        } else {
            if (document.getLastUpdate() != null) {
                updateLogRepository.save(document.getLastUpdate());
            }

            document.setLastUpdate(updateLog);
        }

        documentRepository.save(document);
    }

    /**
     * Updates document's parent folder.
     * @param documentId
     * @param parentId
     * @return
     */
    public Document setParent(int documentId, int parentId) {
        Document document = this.documentsCache.get(documentId);
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
     * @param documentId
     * @param title
     * @return
     */
    public Document setTitle(int documentId, String title) {
        Document document = this.documentsCache.get(documentId);
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
        try {
            Document document = this.documentsCache.get(documentId);

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
        Document document = createDocument(ownerId, parentID, getFileName(path));
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
            updateLogRepository.deleteByDocumentId(documentId);
            documentRepository.delete(document.get());
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

    private void updateContent(int documentId, String content) {
        Document document = this.documentsCache.get(documentId);
        document.setContent(content);
        documentRepository.save(document);
    }

    private String generateUrl(int documentId) {
        Document document = this.documentsCache.get(documentId);

        String url = document.getMetadata().getTitle();
        int parentId = document.getMetadata().getParentId();

        Optional<Folder> parent = folderRepository.findById(parentId);

        while (parent.isPresent()) {
            url = parent.get().getMetadata().getTitle() + FileSystems.getDefault().getSeparator() + url;
            parent = folderRepository.findById(parent.get().getMetadata().getParentId());
        }

        return url;
    }
}