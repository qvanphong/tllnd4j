package tech.qvanphong.tllnbot.commands.market;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

@Component
public class BTCDominanceCommand implements SlashCommand {
    private final CoinGeckoApiClient coinGeckoApiClient;

    public BTCDominanceCommand(CoinGeckoApiClient coinGeckoApiClient) {
        this.coinGeckoApiClient = coinGeckoApiClient;
    }

    @Override
    public String getName() {
        return "dmn";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return Mono.justOrEmpty(coinGeckoApiClient.getGlobal())
                .flatMap(global -> event.reply(
                        interactionSpec -> interactionSpec.addEmbed(
                                embedCreateSpec ->
                                        embedCreateSpec.setColor(Color.of(13, 222, 251))
                                .addField("Dominance", String.format("%.2f", global.getData().getMarketCapPercentage().get("btc")), true)
                                .addField("Thay đổi trong 24h", String.format("%.2f", global.getData().getMarketCapChangePercentage24hUsd()), true)
                        )));
    }
}
