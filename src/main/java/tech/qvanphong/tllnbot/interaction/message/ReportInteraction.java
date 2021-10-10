package tech.qvanphong.tllnbot.interaction.message;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.discordjson.json.*;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.RestClient;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.config.BotConfig;
import tech.qvanphong.tllnbot.interaction.MessageInteractionCommand;

@Component
public class ReportInteraction implements MessageInteractionCommand {
    private RestClient restClient;
    private BotConfig botConfig;

    @Autowired
    public ReportInteraction(RestClient restClient, BotConfig botConfig) {
        this.restClient = restClient;
        this.botConfig = botConfig;
    }

    @Override
    public String getConfigName() {
        return "interaction_report";
    }

    @Override
    public String getName() {
        return "Report";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {
        return event.getTargetMessage()
                .flatMap(message -> {
                    boolean hasAuthor = message.getAuthor().isPresent();
                    long reportChannelId = botConfig.getReportChannelId();
                    String authorName = hasAuthor ? message.getAuthor().get().getUsername() : "Unknown";
                    String avatar = hasAuthor ? message.getAuthor().get().getAvatarUrl() : "";
                    String messageContent = message.getContent();
                    String url = "";
                    String attachmentUrl = message.getAttachments().size() > 0 ? message.getAttachments().get(0).getUrl() : "";

                    if (event.getInteraction().getGuildId().isPresent()) {
                        url = String.format("https://discord.com/channels/%d/%d/%d",
                                event.getInteraction().getGuildId().get().asLong(),
                                message.getChannelId().asLong(),
                                message.getId().asLong());
                    }


                    ImmutableEmbedData embedMessage = ImmutableEmbedData.builder()
                            .color(Color.of(255,204,0).getRGB())
                            .author(ImmutableEmbedAuthorData.builder().name(authorName).iconUrl(avatar).build())
                            .description(messageContent)
                            .addField(ImmutableEmbedFieldData.of("Nguồn", "[Cổng dịch chuyển ( O )](" + url + ")", Possible.of(false)))
                            .image(ImmutableEmbedImageData.builder().url(attachmentUrl).build())
                            .footer(ImmutableEmbedFooterData.builder().text(message.getId().asString()).build())
                            .build();

                    return event.reply(String.format("<#%d>", reportChannelId))
                            .then(restClient.getChannelById(Snowflake.of(reportChannelId))
                                    .createMessage(ImmutableMessageCreateRequest
                                            .builder()
                                            .content(String.format("⚠️ <#%d>", message.getChannelId().asLong()))
                                            .embed(embedMessage).build())
                                    .then());
                });
    }
}
