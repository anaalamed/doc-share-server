package docSharing.service;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.File;
import docSharing.entities.document.Folder;
import docSharing.repository.FileRepository;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    private final FileRepository fileRepository;

    private FileService(FileRepository documentRepository) {
        this.fileRepository = documentRepository;
    }

    /*public void join(User user, String url) {
        File file = fileRepository.findByUrl(url);
        file.addActiveUser(user);
        fileRepository.save(file);
    }

    public void leave(User user, String url) {
        File file = fileRepository.findByUrl(url);
        file.removeActiveUser(user);
        fileRepository.save(file);
    }

    public void update(String url, UpdateRequest updateRequest) {
        File file = fileRepository.findByUrl(url);
        if (file instanceof Document) {
            Document document = (Document) file;
            document.updateContent(updateRequest);
        }
    }*/

    public void createDocument(User owner, Folder parent, String title, String url) {
        Document document = new Document(owner, parent, title, url);
        fileRepository.save(document);
    }

    public void createFolder(User owner, Folder parent, String title, String url) {
        Folder folder = new Folder(owner, parent, title, url);
        fileRepository.save(folder);
    }

    public void delete(String url, User user) {
        File file = fileRepository.findByUrl(url);
        if (!(file.getAuthorized().get(Permission.OWNER).contains(user)
                || file.getAuthorized().get(Permission.EDITOR).contains(user))) {
            throw new IllegalArgumentException(String.format("User with id %d is not authorized", user.getId()));
        }

        if (file instanceof Folder) {
            for (File subFile : ((Folder) file).getSubFiles()) {
                this.delete(subFile.getUrl(), user);
            }
        }

        fileRepository.delete(file);
    }

    public void updatePermission(String url, User owner, User user, Permission permission) {
        File file = fileRepository.findByUrl(url);
        if (!file.getAuthorized().get(Permission.OWNER).contains(owner)) {
            throw new IllegalArgumentException(String.format("User with id %d is not the file's owner", owner.getId()));
        }

        file.updatePermission(user, permission);
        fileRepository.save(file);
    }
}
