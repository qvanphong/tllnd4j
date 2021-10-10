package tech.qvanphong.tllnbot.commands.image;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.qvanphong.tllnbot.commands.SlashCommand;
import tech.qvanphong.tllnbot.entity.ImageCommand;
import tech.qvanphong.tllnbot.service.ImageCommandService;

import java.util.regex.Pattern;

@Component
public class AddImageCommand implements SlashCommand {
    @Autowired
    private ImageCommandService commandService;
    
    private final Pattern mediaLinkPattern = Pattern.compile("https:\\/\\/(cdn\\.discordapp\\.com|media\\.discordapp\\.net)\\/attachments\\/\\d+\\/\\d+\\/\\S+", Pattern.MULTILINE);
    private final Pattern customImageCommandPattern = Pattern.compile("^[a-zA-Z0-9]*$", Pattern.MULTILINE);

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String commandName = event.getOption("command").flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString).orElse("");

        String imageLink = event.getOption("image_link").flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString).orElse("");

        if (commandName.isEmpty() || imageLink.isEmpty())
            return Mono.empty();
        
        if (!customImageCommandPattern.matcher(commandName).find()) {
            return event.reply("Tên lệnh không hợp lệ (chỉ chứa ký tự a - z, A - Z, 0 - 9)");
        }

        if (!mediaLinkPattern.matcher(imageLink).find()) {
            return event.reply("Link ảnh không hợp lệ");
        }

        ImageCommand imageCommand = new ImageCommand();
        imageCommand.setCommandName(commandName);
        imageCommand.setContent(imageLink);
        imageCommand.setCreatedBy(event.getInteraction().getUser().getId().asLong());

        return Mono.just(imageCommand)
                .map(toSaveImageCommand -> commandService.saveCommand(imageCommand))
                .flatMap(isSuccess -> {
                    if (isSuccess)
                        return event.reply("**Thêm lệnh thành công**\n" +
                                "**Lệnh**: " + imageCommand.getCommandName() + "\n" +
                                "**Nội dung**: " + imageCommand.getContent() + "\n" +
                                "**Người thêm**: <@!" + imageCommand.getCreatedBy() + ">\n" +
                                "Dùng ~" + imageCommand.getCommandName() + " để bot gửi ảnh.");
                    else
                        return event.reply("Lệnh " + imageCommand.getCommandName() + " đã tồn tại");
                });
    }
}
