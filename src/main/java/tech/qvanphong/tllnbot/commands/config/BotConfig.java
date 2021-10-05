package tech.qvanphong.tllnbot.commands.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bots")
@Getter
@Setter
public class BotConfig {
    /**
     * Bot's token
     */
    private String token;

    /**
     * Server that bot is working on
     */
    private long serverId;

    /**
    * Minimum difference to send an alert (message).
    */
    private double alertDifference;

    /**
     * Price check interval (seconds)
     * */
    private int priceCheckInterval;

    /**
     * Channel Id of reporting behaviour.
     * */
    private long reportChannelId;

    public BotConfig() {
    }

}
