package docSharing.service;

import docSharing.entities.User;
import docSharing.entities.document.Folder;
import docSharing.repository.FolderRepository;
import docSharing.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    private FolderService(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    public Folder createFolder(int ownerId, int parentId, String title) {
        User owner = userRepository.getReferenceById(ownerId);
        Folder parent = folderRepository.getReferenceById(parentId);
        Folder folder = new Folder(owner, parent, title);

        return folderRepository.save(folder);
    }

    public boolean delete(int id) {
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
