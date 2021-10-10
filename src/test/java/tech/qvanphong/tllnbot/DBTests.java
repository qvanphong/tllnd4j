package tech.qvanphong.tllnbot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.qvanphong.tllnbot.entity.ImageCommand;
import tech.qvanphong.tllnbot.repository.ImageCommandRepository;
import tech.qvanphong.tllnbot.service.ImageCommandService;

import java.util.List;

@SpringBootTest
public class DBTests {
    @Autowired
    ImageCommandRepository commandService;

    @Test
    void customImageDBShouldNotBeNull() {
        List<ImageCommand> all = commandService.findAll();
        all.forEach(imageCommand -> System.out.println(imageCommand.getCommandName()));
        assert all.size() != 0;
    }
}
