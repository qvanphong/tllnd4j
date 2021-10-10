package tech.qvanphong.tllnbot.interaction;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.entity.ImageCommand;
import tech.qvanphong.tllnbot.service.ImageCommandService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessageCreateListener {
    @Autowired
    private ImageCommandService service;
    private final String customImageCommandPattern = "~[a-zA-Z0-9]*";
    private final Pattern pattern = Pattern.compile(customImageCommandPattern);


    public Mono<Object> handle(MessageCreateEvent event) {
        // Find custom image command
        if (!event.getMember().isPresent() || event.getMember().get().isBot()) {
            return Mono.empty();
        }

        Matcher matcher = pattern.matcher(event.getMessage().getContent());
        if (matcher.find()) {
            String commandName = matcher.group().replace("~", "");
            ImageCommand savedCommand = service.getCommand(commandName);

            if (savedCommand != null) {
                return event.getMessage()
                        .getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage(savedCommand.getContent()));
            }
        }

        return Mono.empty();
    }
}
