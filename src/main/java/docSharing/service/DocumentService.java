package docSharing.service;

import docSharing.controller.request.ShareRequest;
import docSharing.controller.request.UpdateRequest;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Content;
import docSharing.entities.document.Document;
import docSharing.entities.document.File;
import docSharing.entities.document.Folder;
import docSharing.repository.DocumentRepository;
import docSharing.repository.FolderRepository;
import docSharing.utils.GMailer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import static docSharing.utils.ImportExport.*;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@Configuration
@EnableScheduling
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;

    private DocumentService(DocumentRepository documentRepository, FolderRepository folderRepository) {
        this.documentRepository = documentRepository;
        this.folderRepository = folderRepository;
    }

    static Map<Integer,String> documentCacheChanges = new HashMap<>();//doc id, doc content
    static Map<Integer,String> documentCacheDBContent = new HashMap<>();

    public void update(int documentId, UpdateRequest updateRequest) {
        Document document = documentRepository.getReferenceById(documentId);
        document.updateContent(updateRequest);

        updateContentOnCache(documentId, document.getContent().getContent());
    }

    private void updateContentOnCache(int documentId, String content){
        documentCacheChanges.put(documentId,content);
    }

    private void deleteFromCache(int documentID){
        documentCacheChanges.remove(documentID);
        documentCacheDBContent.remove(documentID);
    }

    @Scheduled(fixedDelay = 10000) //10 seconds
    private void updateContentOnDB(){
        documentCacheChanges.forEach((key, value)->{
            if (!documentCacheDBContent.containsKey(key) || !value.equals(documentCacheDBContent.get(key))) {
                SaveToDB(key, value);
                documentCacheDBContent.put(key, value);//DB cache update
            }
        });
    }

    private void SaveToDB(int documentId, String content){
        Document updateDoc=documentRepository.getReferenceById(documentId).setContent(content);
        documentRepository.save(updateDoc);
    }

    public Document createDocument(int ownerId, int parentId, String title) {
        Document document = new Document(ownerId, parentId, title);
        updateContentOnCache(document.getId(), document.getContent().getContent());
        return documentRepository.save(document);
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

    public boolean delete(int id, int userId) {
        Document document = documentRepository.getReferenceById(id);
        if (!(document.hasPermission(userId, Permission.OWNER) || document.hasPermission(userId, Permission.EDITOR))) {
            return false;
        }

        try {
            documentRepository.delete(document);
            deleteFromCache(document.getId());
        } catch (Exception e) {
            return false;
        }

        return true;
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

    public boolean share(ShareRequest shareRequest) {
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

    public Document importFile(String path, int ownerId, int parentID){
        Document importDocument= new Document(ownerId,parentID, getFileName(path));

        Content content= new Content();
        content.setContent(getContentFromImportFile(path));

        importDocument.setContent(content);
        return importDocument;
    }

    public void exportFile(int documentId){
        Document document = documentRepository.getReferenceById(documentId);

        String filename=document.getMetadata().getTitle();
        String content=document.getContent().toString();

        String pathFile = "C:/Users/Downloads/"+filename+".txt";
        writeToFile(content, pathFile);
    }
}