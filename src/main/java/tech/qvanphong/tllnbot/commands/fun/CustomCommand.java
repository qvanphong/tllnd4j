package tech.qvanphong.tllnbot.commands.fun;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

@Component
public class CustomCommand implements SlashCommand {
    @Override
    public String getName() {
        return "custom_command";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return null;
    }
}
