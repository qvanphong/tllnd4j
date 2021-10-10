package tech.qvanphong.tllnbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.qvanphong.tllnbot.entity.ImageCommand;
import tech.qvanphong.tllnbot.repository.ImageCommandRepository;

@Service
public class ImageCommandService {
    private ImageCommandRepository repository;

    @Autowired
    public ImageCommandService(ImageCommandRepository repository) {
        this.repository = repository;
    }

    public ImageCommand getCommand(String command) {
        return repository.getImageCommandByCommandName(command);
    }

    public boolean saveCommand(ImageCommand command) {
        boolean isExist = repository.existsImageCommandByCommandName(command.getCommandName());
        if (!isExist) {
            repository.save(command);
        }
        return !isExist;
    }
}
