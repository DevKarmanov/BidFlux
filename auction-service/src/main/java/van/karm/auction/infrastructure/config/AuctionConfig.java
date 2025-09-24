package van.karm.auction.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AuctionConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        return new ConcurrentTaskScheduler(executor);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16,32,1,65536,2);
    }
}
