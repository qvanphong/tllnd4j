package tech.qvanphong.tllnbot.commands.fun;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

@Component
public class DuyHuynhCommand implements SlashCommand {
    @Override
    public String getName() {
        return "duyhuynh";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return event.reply("\n" +
                "Duy Huỳnh By Category:\n https://cdn.discordapp.com/attachments/813452767099355136/859710887387987988/DUY_HUYNH_By_Category.pdf");
    }
}
