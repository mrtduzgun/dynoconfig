package com.trendyol.dynoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Murat Duzgun
 * <p>
 * Schedules a task to populate cache from database at given period of time
 */
public class ConfigurationUpdater {
    private final static Logger logger = LoggerFactory.getLogger(ConfigurationUpdater.class);

    private final long refreshTimerIntervalInMs;

    private final ScheduledExecutorService scheduler;

    private final String appName;

    private final ConfigurationDbService configurationDbService;

    private final ConfigurationCacheService configurationCacheService;

    /**
     *  It prevents multiple runs at multiple ConfigurationReader instances
     * */
    private static boolean isRunning = false;

    public ConfigurationUpdater(long refreshTimerIntervalInMs, String appName,
                                ConfigurationDbService configurationDbService,
                                ConfigurationCacheService configurationCacheService) {
        this.refreshTimerIntervalInMs = refreshTimerIntervalInMs;
        this.appName = appName;
        this.configurationDbService = configurationDbService;
        this.configurationCacheService = configurationCacheService;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {

        if (!isRunning) {
            scheduler.scheduleAtFixedRate(new CachePopulater(), refreshTimerIntervalInMs,
                    refreshTimerIntervalInMs, TimeUnit.MILLISECONDS);
            isRunning = true;
        }
    }

    private class CachePopulater implements Runnable {

        @Override
        public void run() {
            logger.debug("Updater is started..");
            configurationCacheService.loadToCache(appName, configurationDbService.getRecords(appName));
            logger.debug("Updater is ended..");
        }
    }
}
