package tech.qvanphong.tllnbot.commands.fun;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

import java.util.Random;

@Component
public class RandomPickCommand implements SlashCommand {
    @Override
    public String getName() {
        return "pick";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return Mono.just(event.getOption("options").flatMap(ApplicationCommandInteractionOption::getValue))
                .map(applicationCommandInteractionOptionValue -> applicationCommandInteractionOptionValue.get().asString())
                .map(optionsMessage -> {
                    String[] options = optionsMessage.split(",");
                    return options[new Random().nextInt(options.length)];
                })
                .flatMap(result -> event.reply(spec -> spec.addEmbed(
                        embedCreateSpec ->
                                embedCreateSpec.setTitle("\uD83C\uDF89 Kết quả ngẫu nhiên \uD83C\uDF89")
                                        .setDescription(result.trim())
                )));
    }
}
