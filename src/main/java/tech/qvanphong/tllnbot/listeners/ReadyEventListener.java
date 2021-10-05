package tech.qvanphong.tllnbot.listeners;

import com.google.gson.Gson;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.discordjson.json.*;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.RestClient;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.qvanphong.tllnbot.commands.config.BotConfig;
import tech.qvanphong.tllnbot.commands.config.PriceBotsConfig;
import tech.qvanphong.tllnbot.dto.CoinBot;
import tech.qvanphong.tllnbot.dto.CoinTradingTicker;
import tech.qvanphong.tllnbot.entity.Coin;
import tech.qvanphong.tllnbot.service.CoinService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ReadyEventListener {

    private final String BINANCE_24h_TICKER_URL = "https://api3.binance.com/api/v3/ticker/24hr";
    private final Map<String, String> coinSymbolByTradingPair = new HashMap<>();
    private final Gson gson = new Gson();
    private final RestClient restClient;
    private final CoinService coinService;
    private final BotConfig botConfig;
    private final PriceBotsConfig priceBotsConfig;
    private double currentBtcPrice = 0.0D;

    public ReadyEventListener(RestClient restClient, CoinService coinService, BotConfig botConfig, PriceBotsConfig priceBotsConfig) {
        this.restClient = restClient;
        this.coinService = coinService;
        this.botConfig = botConfig;
        this.priceBotsConfig = priceBotsConfig;

        coinSymbolByTradingPair.put("BTCUSDT", "BTC");
        coinSymbolByTradingPair.put("NEOUSDT", "NEO");
        coinSymbolByTradingPair.put("FIROUSDT", "FIRO");
        coinSymbolByTradingPair.put("DASHUSDT", "DASH");
        coinSymbolByTradingPair.put("ZENUSDT", "ZEN");
        coinSymbolByTradingPair.put("ETHUSDT", "ETH");
        coinSymbolByTradingPair.put("ARKBTC", "ARK");
        coinSymbolByTradingPair.put("GASBTC", "GAS");
    }

    public Mono<Void> handle(ReadyEvent readyEvent) {
        return Flux.interval(Duration.ofSeconds(botConfig.getPriceCheckInterval()))
                .flatMap(tick -> Flux.just(coinSymbolByTradingPair.keySet().toArray(new String[0])))
                // Get coin trading market
                .flatMap(this::fetchCoinTradingPair)
                // Convert fromJson to Pojo
                .map(tradingResult -> gson.fromJson(tradingResult, CoinTradingTicker.class))
                // Rename bot follow current price, then check if it should send message or not.
                .flatMap(coinTradingTicker ->
                        Mono.when(renamePriceBot(coinTradingTicker))
                                .then(sendMessageOrNot(coinTradingTicker)))
                // If there is a Coin object "fall from upstream" which mean need to update it to Database
                .map(coinService::saveOrUpdateToDatabase)
                .then();
    }

    private Mono<String> fetchCoinTradingPair(String coinTradingPair) {
        String coinTradingPairURL = BINANCE_24h_TICKER_URL + "?symbol=" + coinTradingPair;
        return HttpClient.create()
                .get()
                .uri(coinTradingPairURL)
                .responseContent()
                .aggregate()
                .asString();
    }

    private Mono<Coin> sendMessageOrNot(CoinTradingTicker coinTradingTicker) {
        if (coinTradingTicker.getSymbol().equals("BTCUSDT")) {
            this.currentBtcPrice = Double.parseDouble(coinTradingTicker.getLastPrice());
        }

        boolean isBTCTradingPair = isBTCTradingPair(coinTradingTicker.getSymbol());
        // Get old price in database
        String coinName = coinSymbolByTradingPair.get(coinTradingTicker.getSymbol());
        Coin coin = coinService.getCoinByName(coinName);
        CoinBot coinBot = getBotByCoinName(coinName);

        // Get latest price, in case it's in BTC trading pair, convert it to USD price
        // If current BTC price isn't saved, return empty.
        double latestPrice = Double.parseDouble(coinTradingTicker.getLastPrice());
        latestPrice = isBTCTradingPair ? convertToUsd(latestPrice) : latestPrice;

        if (latestPrice == 0) {
            return Mono.empty();
        }

        // Coin is null which mean it isn't exist in database, create a new coin object
        // and pass to downstream to save it as the first time
        if (coin == null) {
            Coin newCoin = new Coin();
            newCoin.setCoinName(coinName);
            newCoin.setUsdPrice(latestPrice);

            return Mono.just(newCoin);
        }

        // Compare the old price with the latest price, if it have met the minimum difference,
        // then send. Otherwise, return empty.
        if (coinBot != null && coinBot.getAlertChannelId() != 0 && shouldAlert(coin.getUsdPrice(), latestPrice)) {
            boolean isPump = coin.getUsdPrice() < latestPrice;
            double difference = getDifference(coin.getUsdPrice(), latestPrice);

            ImmutableEmbedData embedMessage = ImmutableEmbedData.builder()
                    .author(ImmutableEmbedAuthorData.builder().name("Price Alert").iconUrl(coinBot.getIconUrl()).build())
                    .description(String.format("**%s** vừa %s %.2f%% trên sàn Binance",
                            coin.getCoinName(),
                            isPump ? "tăng" : "giảm",
                            difference))
                    .thumbnail(ImmutableEmbedThumbnailData.builder().url(coinBot.getIconUrl()).build())
                    .color(Color.of(13, 222, 251).getRGB())
                    .addField(ImmutableEmbedFieldData.of("Giá trước đó", String.format("%.2f USD", coin.getUsdPrice()), Possible.of(true)))
                    .addField(ImmutableEmbedFieldData.of("Giá hiện tại", String.format("%.2f USD", latestPrice), Possible.of(true)))
                    .addField(ImmutableEmbedFieldData.of("Thay đổi trong 24h", coinTradingTicker.getPriceChangePercent() + "%", Possible.of(false)))
                    .build();

            coin.setUsdPrice(latestPrice);

            return restClient.getChannelById(Snowflake.of(877141826476331030L))
                    .createMessage(embedMessage)
                    .onErrorResume(throwable -> Mono.empty())
                    .then(Mono.just(coin));
        }

        return Mono.empty();
    }

    private Mono<CoinTradingTicker> renamePriceBot(CoinTradingTicker coinTradingTicker) {
        String coinName = coinSymbolByTradingPair.get(coinTradingTicker.getSymbol());
        boolean isBTCTradingPair = isBTCTradingPair(coinTradingTicker.getSymbol());
        CoinBot coinBot = getBotByCoinName(coinName);

        if (coinBot != null) {
            double lastPrice = Double.parseDouble(coinTradingTicker.getLastPrice());
            lastPrice = isBTCTradingPair ? convertToUsd(lastPrice) : lastPrice;
            String formattedLastPrice = String.format("%.2f", lastPrice);

            ImmutableGuildMemberModifyRequest modifyRequest = ImmutableGuildMemberModifyRequest.builder()
                    .nick(Possible.of(Optional.of(coinName + " $" + formattedLastPrice)))
                    .build();


            return restClient.getGuildById(Snowflake.of(botConfig.getServerId()))
                    .modifyMember(Snowflake.of(coinBot.getBotId()), modifyRequest, "Update Coin Bot Price")
                    .onErrorResume(throwable -> Mono.empty())
                    .then(Mono.just(coinTradingTicker));
        }

        return Mono.just(coinTradingTicker);
    }

    private boolean shouldAlert(double oldPrice, double newPrice) {

        double difference = Math.abs(oldPrice - newPrice) / ((oldPrice + newPrice) / 2) * 100;

        return difference >= botConfig.getAlertDifference();
    }

    private double getDifference(double oldPrice, double newPrice) {
        return Math.abs(oldPrice - newPrice) / ((oldPrice + newPrice) / 2) * 100;
    }

    private CoinBot getBotByCoinName(String coinName) {
        switch (coinName) {
            case "BTC":
                return priceBotsConfig.getBtc();
            case "NEO":
                return priceBotsConfig.getNeo();
            case "FIRO":
                return priceBotsConfig.getFiro();
            case "DASH":
                return priceBotsConfig.getDash();
            case "ETH":
                return priceBotsConfig.getEth();
            case "ZEN":
                return priceBotsConfig.getZen();
            case "ARK":
                return priceBotsConfig.getArk();
            case "GAS":
                return priceBotsConfig.getGas();
        }
        return null;
    }

    private boolean isBTCTradingPair(String symbol) {
        return !symbol.equals("BTCUSDT") && symbol.contains("BTC");
    }

    private double convertToUsd(double btcPrice) {
        return btcPrice * currentBtcPrice;
    }
}
