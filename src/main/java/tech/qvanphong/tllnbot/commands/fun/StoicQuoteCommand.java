package tech.qvanphong.tllnbot.commands.fun;

import com.google.gson.Gson;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.qvanphong.tllnbot.commands.SlashCommand;
import tech.qvanphong.tllnbot.dto.StoicQuote;

@Component
public class StoicQuoteCommand implements SlashCommand {
    @Override
    public String getName() {
        return "stoic";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {

        return HttpClient
                .create()
                .get()
                .uri("https://stoic-quotes.com/api/quote")
                .responseContent()
                .aggregate()
                .asString()
                .map(responseBody -> new Gson().fromJson(responseBody, StoicQuote.class))
                .flatMap(stoicQuote -> event.reply(String.format("**\"%s\"** - *%s*", stoicQuote.getText(), stoicQuote.getAuthor())));
    }
}
