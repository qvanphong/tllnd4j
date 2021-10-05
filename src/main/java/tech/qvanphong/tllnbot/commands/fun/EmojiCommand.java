package tech.qvanphong.tllnbot.commands.fun;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;
import tech.qvanphong.tllnbot.dto.Emoji;

import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmojiCommand implements SlashCommand {
    @Override
    public String getName() {
        return "emoji";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return Mono
                .justOrEmpty(event.getOption("emoji").flatMap(ApplicationCommandInteractionOption::getValue))
                .flatMap(applicationCommandInteractionOptionValue -> {
                    String emojiMessage = applicationCommandInteractionOptionValue.asString().trim();
                    Emoji emoji = parseEmoji(emojiMessage);
                    if (emoji != null)
                        return Mono.just(emoji);

                    return Mono.error(new InputMismatchException("Input emoji is not as expected"));
                })
                .flatMap(emoji -> event.reply(String.format("https://cdn.discordapp.com/emojis/%s.%s", emoji.getId(), emoji.getFileType())))
                .onErrorResume(throwable -> event.reply("Emoji nhập vào không hợp lệ"));
    }


    private Emoji parseEmoji(String emojiMessage) {
        Pattern pattern = Pattern.compile("^<a?:\\S*:([0-9]*)>$");
        Matcher matcher = pattern.matcher(emojiMessage);
        if (matcher.find()) {
            return new Emoji(emojiMessage.contains("<a:"), Long.parseLong(matcher.group(1)));
        }
        return null;
    }

}
