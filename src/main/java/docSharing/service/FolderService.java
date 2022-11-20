package docSharing.service;

import docSharing.entities.User;
import docSharing.entities.document.Folder;
import docSharing.repository.FolderRepository;
import org.springframework.stereotype.Service;


@Service
public class FolderService {
    private final FolderRepository folderRepository;

    private FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public Folder createFolder(User owner, Folder parent, String title) {
        Folder folder = new Folder(owner, parent, title);
        return folderRepository.save(folder);
    }

    public boolean delete(int id, User user) {
        boolean success = true;

        Folder folder = folderRepository.getReferenceById(id);

        try {
            folderRepository.delete(folder);
        } catch (Exception e) {
            success = false;
        }

        return success;
    }
}
