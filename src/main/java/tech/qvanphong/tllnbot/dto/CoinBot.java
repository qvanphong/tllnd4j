package tech.qvanphong.tllnbot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public @Data
class CoinBot {
    /*
    * Discord ID of Bot for recognize, find by Discord
    * */
    private long botId;
    /**
    * Channel ID that will send a message pump or dump when current price and old price met minimum difference.
    * */
    private long alertChannelId;
    /*
    * Coin's icon URL.
    * */
    private String iconUrl;

}
