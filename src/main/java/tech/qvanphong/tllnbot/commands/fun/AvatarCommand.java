package tech.qvanphong.tllnbot.commands.fun;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

@Component
public class AvatarCommand implements SlashCommand {
    @Override
    public String getName() {
        return "avatar";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return Mono.just(event.getOption("user")
                    .flatMap(ApplicationCommandInteractionOption::getValue))
                .flatMap(optionValue -> optionValue.get().asUser())
                .flatMap(user -> event.reply(user.getAvatarUrl() + "?size=1024"));
    }
}
