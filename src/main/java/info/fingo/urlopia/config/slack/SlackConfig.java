package info.fingo.urlopia.config.slack;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {
    @Value("${slack.signing-secret}")
    private String signingSecret;

    @Value("${slack.bot-token}")
    private String botToken;

    @Bean
    public App initSlackApp() {
        var config = new AppConfig();
        config.setSigningSecret(signingSecret);
        config.setSingleTeamBotToken(botToken);
        return new App(config);
    }
}
