package tech.qvanphong.tllnbot;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.CoinList;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.config.BotConfig;
import tech.qvanphong.tllnbot.interaction.MessageCreateListener;
import tech.qvanphong.tllnbot.listeners.MessageInteractionListener;
import tech.qvanphong.tllnbot.listeners.ReadyEventListener;
import tech.qvanphong.tllnbot.listeners.SlashCommandListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {


    @Bean
    public RestClient restClient(BotConfig botConfig) {
        return RestClient.create(botConfig.getToken());
    }

    @Bean
    public CoinGeckoApiClient coinGeckoApiClient() {
        return new CoinGeckoApiClientImpl();
    }

    /*
     * Remapped coin symbol as Key and its name as Value
     * */
    @Bean
    public Map<String, String> coinBySymbol() {
        CoinGeckoApiClient coinGeckoApiClient = this.coinGeckoApiClient();
        List<CoinList> coinList = coinGeckoApiClient.getCoinList();

        Map<String, String> coinBySymbol = new HashMap<>();
        coinList.forEach(coin -> coinBySymbol.put(coin.getSymbol(), coin.getId()));
        coinBySymbol.put("usd", "tether");
        coinBySymbol.put("vnd", "binance-vnd");

        return coinBySymbol;
    }

    @Bean
    public GatewayDiscordClient discordClient(BotConfig botConfig,
                                              SlashCommandListener slashCommandListener,
                                              ReadyEventListener readyEventListener,
                                              MessageInteractionListener messageInteractionListener,
                                              MessageCreateListener messageCreateListener) {
        String token = botConfig.getToken();
        // Login
        return DiscordClientBuilder.create(token).build()
                .gateway()
                .withEventDispatcher(eventDispatcher -> {
                    Flux<Void> slashCommandFlux = eventDispatcher
                            .on(ChatInputInteractionEvent.class)
                            .flatMap(slashCommandListener::handle);

                    Flux<Void> onReadyFlux = eventDispatcher
                            .on(ReadyEvent.class)
                            .flatMap(readyEventListener::handle);

                    Flux<Void> messageInteractionFlux = eventDispatcher
                            .on(MessageInteractionEvent.class)
                            .flatMap(messageInteractionListener::handle);

                    Flux<Object> messageCreateEventFlux = eventDispatcher.on(MessageCreateEvent.class)
                            .flatMap(messageCreateListener::handle);

                    Flux<DisconnectEvent> onDisconnect = eventDispatcher.on(DisconnectEvent.class)
                            .flatMap(disconnectEvent -> {
                                System.out.println(disconnectEvent);
                                return Mono.just(disconnectEvent);
                            });


                    return Mono.when(onReadyFlux, messageCreateEventFlux, messageInteractionFlux, slashCommandFlux, onDisconnect);
                })
                .login()
                .block();
    }

}
