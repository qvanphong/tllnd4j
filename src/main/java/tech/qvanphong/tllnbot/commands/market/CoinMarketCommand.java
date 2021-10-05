package tech.qvanphong.tllnbot.commands.market;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.CoinFullData;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.rest.util.Color;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CoinMarketCommand extends CoinPriceCommand{

    public CoinMarketCommand(Map<String, String> coinBySymbol, CoinGeckoApiClient coinGeckoApiClient) {
        super(coinBySymbol, coinGeckoApiClient);
    }

    @Override
    public String getName() {
        return "mk";
    }

    @Override
    InteractionApplicationCommandCallbackSpec createReplyMessage(CoinFullData coinData) {
        String totalSupply = coinData.getMarketData().getTotalSupply() == 0 ? "∞" : String.format("%,d", coinData.getMarketData().getTotalSupply());
        String detailSource = "Tổng cung: ${totalSupply} ${symbol}\n" +
                "Tổng cung lưu hành: ${circulatingSupply} ${symbol}\n" +
                "Vốn hóa: $${marketCap} USD\n" +
                "Khối lượng giao dịch: $${tradeVolume} USD";
        String priceSource = "Hiện tại: $${currentPrice} USD\n" +
                "ATH: $${ath} USD";
        String priceChangeSource = "Ngày: ${dailyChange}%\n" +
                "Tuần: ${weeklyChange}%\n" +
                "Tháng: ${monthlyChange}%\n";

        HashMap<String, String> stringFormatPlaceHolder = new HashMap<>();
        stringFormatPlaceHolder.put("totalSupply", totalSupply);
        stringFormatPlaceHolder.put("circulatingSupply", String.format("%,.2f", coinData.getMarketData().getCirculatingSupply()));
        stringFormatPlaceHolder.put("symbol", coinData.getSymbol().toUpperCase());
        stringFormatPlaceHolder.put("marketCap", String.format("%,.2f", coinData.getMarketData().getMarketCap().get("usd")));
        stringFormatPlaceHolder.put("tradeVolume", String.format("%,.2f", coinData.getMarketData().getTotalVolume().get("usd")));
        stringFormatPlaceHolder.put("currentPrice", String.format("%,.2f", coinData.getMarketData().getCurrentPrice().get("usd")));
        stringFormatPlaceHolder.put("ath", String.format("%,.2f", coinData.getMarketData().getAth().get("usd")));
        stringFormatPlaceHolder.put("dailyChange", String.format("%,.2f", coinData.getMarketData().getPriceChangePercentage24h()));
        stringFormatPlaceHolder.put("weeklyChange", String.format("%,.2f", coinData.getMarketData().getPriceChangePercentage7d()));
        stringFormatPlaceHolder.put("monthlyChange", String.format("%,.2f", coinData.getMarketData().getPriceChangePercentage30d()));

        StringSubstitutor sub = new StringSubstitutor(stringFormatPlaceHolder, "${", "}", '\\');
        sub.setPreserveEscapes(true);

        String detail = sub.replace(detailSource);
        String price = sub.replace(priceSource);
        String priceChange = sub.replace(priceChangeSource);

        EmbedCreateSpec embedMessage = EmbedCreateSpec
                .builder()
                .color(Color.of(13, 222, 251))
                .author(String.format("%s (%s)", coinData.getName(), stringFormatPlaceHolder.get("symbol")),
                        null, coinData.getImage().getThumb())
                .thumbnail(coinData.getImage().getLarge())
                .description(
                        String.format("**%s** được xếp hạng **#%d** trên CoinGecko",
                                stringFormatPlaceHolder.get("symbol"),
                                coinData.getCoingeckoRank()))
                .addField("Chi tiết", detail, false)
                .addField("Giá", price, true)
                .addField("Thay đổi", priceChange, true)
                .build();
        return InteractionApplicationCommandCallbackSpec
                .builder()
                .addEmbed(embedMessage)
                .build();

    }
}
