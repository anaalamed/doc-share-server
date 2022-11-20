package docSharing.service;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.File;
import docSharing.repository.FileRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    private final FileRepository fileRepository;

    private DocumentService(FileRepository documentRepository) {
        this.fileRepository = documentRepository;
    }

    public void join(User user, String url) {
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
    }
}
