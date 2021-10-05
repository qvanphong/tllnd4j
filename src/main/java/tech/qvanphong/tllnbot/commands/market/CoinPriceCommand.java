package tech.qvanphong.tllnbot.commands.market;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.CoinFullData;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

import java.util.Map;
import java.util.Optional;

@Component
public class CoinPriceCommand implements SlashCommand {
    private final Map<String, String> coinBySymbol;
    private final CoinGeckoApiClient coinGeckoApiClient;


    public CoinPriceCommand(Map<String, String> coinBySymbol, CoinGeckoApiClient coinGeckoApiClient) {
        this.coinBySymbol = coinBySymbol;
        this.coinGeckoApiClient = coinGeckoApiClient;
    }

    @Override
    public String getName() {
        return "price";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Mono<Void> replyNotFoundMono = event.reply("Không tìm thấy coin đã nhập.");

        // If coin not found, return an Mono type that reply a coin name not found message.
        boolean hasCoinName = event
                .getOption("coin_name")
                .filter(option -> option.getValue().isPresent() && coinBySymbol.get(option.getValue().get().asString().toLowerCase()) != null)
                .isPresent();

        if (hasCoinName) {
            Optional<String> coinNameOptional = event.getOption("coin_name")
                    .flatMap(ApplicationCommandInteractionOption::getValue)
                    .map(optionValue -> optionValue.asString().toLowerCase());

            return Mono.justOrEmpty(coinNameOptional)
                    .flatMap(coinName -> {
                        CoinFullData coinData = coinGeckoApiClient.getCoinById(coinName, false, false, true, false, false, false);
                        return Mono.justOrEmpty(coinData);
                    })
                    .flatMap(coinData -> event.reply(createReplyMessage(coinData)));
        } else {
            return replyNotFoundMono;
        }
    }

    InteractionApplicationCommandCallbackSpec createReplyMessage(CoinFullData coinData) {
        EmbedCreateSpec embedMessage = EmbedCreateSpec.builder()
                .color(Color.of(13, 222, 251))
                .author(coinData.getSymbol().toUpperCase(), null, coinData.getImage().getThumb())
                .title(
                        String.format("$%.2f *(%.2f%%)*", coinData.getMarketData().getCurrentPrice().get("usd"),
                                coinData.getMarketData().getPriceChange24h()))
                .footer("Sử dụng !mk ark để xem thêm chi tiết", null).build();
        return InteractionApplicationCommandCallbackSpec
                .builder()
                .addEmbed(embedMessage)
                .build();
    }
}
