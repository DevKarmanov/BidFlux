package van.karm.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AppConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        return new ConcurrentTaskScheduler(executor);
    }
}
