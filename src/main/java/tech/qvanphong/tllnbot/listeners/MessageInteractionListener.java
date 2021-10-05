package tech.qvanphong.tllnbot.listeners;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.interaction.MessageInteractionCommand;

import java.util.Collection;

@Component
public class MessageInteractionListener {
    private Collection<MessageInteractionCommand> commands;

    @Autowired
    public MessageInteractionListener(ApplicationContext context) {
        commands = context.getBeansOfType(MessageInteractionCommand.class).values();
    }

    public Mono<Void> handle(MessageInteractionEvent event) {
        return Flux.fromIterable(commands)
                .filter(command -> command.getName().equals(event.getCommandName()))
                .next()
                .flatMap(messageInteractionCommand -> messageInteractionCommand.handle(event));
    }
}
