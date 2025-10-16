package van.karm.auth.infrastructure.service.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import van.karm.auth.application.service.schedule.UserScheduler;
import van.karm.auth.infrastructure.cleaner.Cleaner;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UserSchedulerImpl implements UserScheduler {
    private final TaskScheduler taskScheduler;
    private final Cleaner cleaner;

    @Value("${settings.scheduler.clean.accounts.execute}")
    private boolean cleanAccounts;

    @Value("${settings.scheduler.clean.accounts.repeat-time-days}")
    private int cleanAccountsRepeatTime;

    @PostConstruct
    public void init() {
        if (cleanAccounts){
            taskScheduler.scheduleAtFixedRate(this::start, Duration.ofDays(cleanAccountsRepeatTime));
        }
    }

    @Override
    public void start() {
        cleaner.clean();
    }
}
