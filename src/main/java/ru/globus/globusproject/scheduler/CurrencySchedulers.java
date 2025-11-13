package ru.globus.globusproject.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.globus.globusproject.service.interfaces.CurrencyUpdateService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencySchedulers {

    private final CurrencyUpdateService updateService;

    @Scheduled(cron = "0 25 12 * * *", zone = "Europe/Moscow")
    public void fakeUpdate() {
        log.info("[FAKE SCHEDULER] The currency update launch was successful.");
    }

    @Scheduled(cron = "0 30 12 * * *", zone = "Europe/Moscow")
    public void realUpdate() {
        log.info("[REAL SCHEDULER] Launch of currencies update...");
        updateService.updateCurrencies();
    }
}
