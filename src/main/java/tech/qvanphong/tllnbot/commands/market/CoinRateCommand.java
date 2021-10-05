package tech.qvanphong.tllnbot.commands.market;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Rate command.
 */
@Component
public class CoinRateCommand implements SlashCommand {
    private final CoinGeckoApiClient coinGeckoApiClient;
    private final Map<String, String> coinBySymbol;

    /**
     * Instantiates a new Rate command.
     *
     * @param coinGeckoApiClient the coin gecko api client
     * @param coinBySymbol       the coin by symbol
     */
    public CoinRateCommand(CoinGeckoApiClient coinGeckoApiClient, Map<String, String> coinBySymbol) {
        this.coinGeckoApiClient = coinGeckoApiClient;
        this.coinBySymbol = coinBySymbol;
    }

    @Override
    public String getName() {
        return "rate";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        // 2 coin name option coin_a and coin_b
        String coinA = getCoinName(event, "coin_a");
        String coinB = getCoinName(event, "coin_b");

        if (coinBySymbol.get(coinA) == null || coinBySymbol.get(coinB)  == null)
            return event.reply("Coin nhập vào không hợp lệ");

        // 'amount' is optional option, if it not exist, use 1 as default
        float amount = getAmount(event.getOption("amount"));

        return Mono.just(coinA + "," + coinB)
                .map(ids -> coinGeckoApiClient.getPrice(ids, "usd"))
                .flatMap(priceData -> event.reply(spec ->
                        spec.addEmbed(embedCreateSpec -> embedCreateSpec
                                .setColor(Color.of(13, 222, 251))
                                .setTitle(String.format("Rate %s/%s", coinA.toUpperCase(), coinB.toUpperCase()))
                                .setDescription(String.format("%.2f %s = %.2f %s",
                                        amount,
                                        coinA.toUpperCase(),
                                        calculateRate(priceData, coinA, coinB, amount),
                                        coinB.toUpperCase()
                                        ))
                        )));
    }

    private String getCoinName(ChatInputInteractionEvent event, String optionName) {
        Optional<ApplicationCommandInteractionOptionValue> optionalValue = event.getOption(optionName).get().getValue();
        return optionalValue.get().asString().toLowerCase();
    }

    private float getAmount(Optional<ApplicationCommandInteractionOption> option) {
        if (option.isPresent()) {
            Optional<ApplicationCommandInteractionOptionValue> optionValueOptional = option.get().getValue();
            if (optionValueOptional.isPresent()) {
                final String amountPattern = "^([0-9]+(\\.[0-9]*)?)$";
                String amountOption = optionValueOptional.get().asString().trim();
                Pattern pattern = Pattern.compile(amountPattern);
                Matcher matcher = pattern.matcher(amountOption);

                if (matcher.find()) {
                    return Float.parseFloat(matcher.group(0));
                }
            }
        }
        return 1.0f;
    }

    private float calculateRate(Map<String, Map<String, Double>> priceData, String coinA, String coinB, float amount) {
        return (float) (amount * priceData.get(coinA).get("usd") / priceData.get(coinB).get("usd"));
    }
}
