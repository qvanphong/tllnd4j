package tech.qvanphong.tllnbot.commands.fun;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;
import tech.qvanphong.tllnbot.dto.Poll;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PollCommand implements SlashCommand {

    private final String[] regionalEmojis = new String[]{
            "🇦", "🇧", "🇨", "🇩", "🇪", "🇫", "🇬", "🇭", "🇮", "🇯",
            "🇰", "🇱", "🇲", "🇳", "🇴", "🇵", "🇶", "🇷", "🇸", "🇹", "🇺", "🇻", "🇼", "🇽", "🇾", "🇿"
    };

    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        AtomicInteger amountOfOption = new AtomicInteger();
        return Mono.just(event.getOptions())
                .map(applicationCommandInteractionOptions -> {
                    System.out.println("Creating poll");
                    Poll poll = new Poll();
                    for (int index = 0; index < applicationCommandInteractionOptions.size(); index++) {
                        ApplicationCommandInteractionOption option = applicationCommandInteractionOptions.get(index);
                        if (option.getName().equals("question")) {
                            poll.setQuestion(option.getValue().get().asString());
                        } else {
                            final String regionalEmoji = regionalEmojis[index - 1];

                            option.getValue().ifPresent(optionValue -> {
                                poll.addOption(optionValue.asString());
                                poll.appendOptionString(regionalEmoji, optionValue.asString());
                            });

                            amountOfOption.incrementAndGet();
                        }
                    }
                    return poll;
                })
                .flatMap(poll -> {
                            System.out.println("Creating reply");
                            return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                                    .addEmbed(EmbedCreateSpec.builder()
                                            .author("Cuộc thăm dò ý kiến.", null, event.getInteraction().getUser().getAvatarUrl())
                                            .title("\uD83D\uDCCA" + poll.getQuestion() + " ❓")
                                            .description(poll.getOptionsString()).build())
                                    .build());
                        }
                )
                .then(event.getInteractionResponse().getInitialResponse())
                .map(messageData -> new Message(event.getClient(), messageData))
                .flatMapMany(sentMessage -> {
                    System.out.println("Creating Range");
                    return Flux.range(0, amountOfOption.get())
                            .flatMap(optionIndex -> {
                                System.out.println("Reacting " + optionIndex);
                                return sentMessage.addReaction(ReactionEmoji.unicode(regionalEmojis[optionIndex]));
                            });
                })
                .next();

    }
}
