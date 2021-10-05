package tech.qvanphong.tllnbot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TLLNDiscordBotApplication {
    public static void main(String[] args) {
//        SpringApplication.
        new SpringApplicationBuilder(TLLNDiscordBotApplication.class)
                .build()
                .run(args);
    }
}
