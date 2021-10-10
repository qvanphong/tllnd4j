package tech.qvanphong.tllnbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.qvanphong.tllnbot.entity.ImageCommand;

import java.util.List;

@Repository
public interface ImageCommandRepository extends JpaRepository<ImageCommand, String> {
    ImageCommand getImageCommandByCommandName(String commandName);

    boolean existsImageCommandByCommandName(String commandName);

    @Override
    <S extends ImageCommand> S save(S entity);

    @Override
    List<ImageCommand> findAll();
}
