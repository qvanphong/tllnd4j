package tech.qvanphong.tllnbot.interaction;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import reactor.core.publisher.Mono;

public interface MessageInteractionCommand {
    String getConfigName();

    String getName();

    Mono<Void> handle(MessageInteractionEvent event);
}
