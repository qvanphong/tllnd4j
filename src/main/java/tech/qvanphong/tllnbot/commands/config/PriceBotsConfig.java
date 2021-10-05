package tech.qvanphong.tllnbot.commands.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tech.qvanphong.tllnbot.dto.CoinBot;

@ConfigurationProperties(prefix = "price-bot")
@Getter
@Setter
public class PriceBotsConfig {
    @NestedConfigurationProperty
    private CoinBot neo;

    @NestedConfigurationProperty
    private CoinBot gas;

    @NestedConfigurationProperty
    private CoinBot firo;

    @NestedConfigurationProperty
    private CoinBot ark;

    @NestedConfigurationProperty
    private CoinBot dash;

    @NestedConfigurationProperty
    private CoinBot zen;

    @NestedConfigurationProperty
    private CoinBot btc;

    @NestedConfigurationProperty
    private CoinBot eth;

    public PriceBotsConfig() {
    }
}
