package docSharing.service;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.Permission;
import docSharing.entities.document.Document;
import docSharing.entities.document.File;
import docSharing.entities.document.Folder;
import docSharing.repository.DocumentRepository;
import docSharing.repository.FolderRepository;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

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

    public boolean updatePermission(int id, int ownerId, int userId, Permission permission) {
        Document document = documentRepository.getReferenceById(id);
        if (!document.hasPermission(ownerId, Permission.OWNER)) {
            return false;
        }

        document.updatePermission(userId, permission);
        Document savedDocument = documentRepository.save(document);

        return savedDocument.hasPermission(userId, permission);
    }

    public String shareByLink(int id) {
        Document document = documentRepository.getReferenceById(id);
        return generateUrl(id);
    }

    public boolean shareByEmail(int id, int ownerId, int userId, Permission permission) {
        updatePermission(id, ownerId, userId, permission);

        // TODO: send email if email failed return false
        return true;
    }

        private String generateUrl(int id) {
            File file = documentRepository.getReferenceById(id);
            String url = file.getMetadata().getTitle();

            while (file.getMetadata().getParentId() > 0) {
                Folder parent = folderRepository.getReferenceById(file.getMetadata().getParentId());

                url = parent.getMetadata().getTitle() + FileSystems.getDefault().getSeparator() + url;
                file = parent;
            }

        return url;
    }

    public void exportFile(String content){
        String fileToWrite = "C:/Users/Desktop/ExportedDataFromDoc.txt";
        FileWriter fw;
        try {
            fw = new FileWriter(fileToWrite);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String importFile(String path){
        String str = "";
        try {
            str = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

}