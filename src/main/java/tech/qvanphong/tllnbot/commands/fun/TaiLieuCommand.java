package tech.qvanphong.tllnbot.commands.fun;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

@Component
public class TaiLieuCommand implements SlashCommand {
    @Override
    public String getName() {
        return "tailieu";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return event.reply("Tài liệu về Stoic, Carl Jung, Duy Huỳnh, Nguyễn Duy Cần,... trên Trở lại làm người:\n https://www.facebook.com/permalink.php?story_fbid=112580303705418&id=109294147367367");
    }
}
