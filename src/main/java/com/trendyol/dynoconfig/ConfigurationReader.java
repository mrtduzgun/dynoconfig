package com.trendyol.dynoconfig;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Murat Duzgun
 * <p>
 * The main class has opened to outside, read to get updated configuration value
 */
public class ConfigurationReader {
    private final static Logger logger = LoggerFactory.getLogger(ConfigurationReader.class);

    private final String applicationName;

    private final ConfigurationCacheService configurationCacheService;

    private final ConfigurationDbService configurationDbService;

    public ConfigurationReader(String applicationName, String dbConnectionString) {

        if (StringUtils.isBlank(applicationName) || StringUtils.isBlank(dbConnectionString)) {
            throw new IllegalArgumentException("Application name and database connection string must be valid!");
        }

        long refreshTimerIntervalInMs = getRefreshTimerIntervalInMs();

        if (refreshTimerIntervalInMs < 100)
            throw new IllegalArgumentException("Timer interval must greater than or equal to 100ms!");

        this.applicationName = applicationName;

        this.configurationCacheService = new ConfigurationCacheService();
        this.configurationDbService = new ConfigurationDbService(dbConnectionString);

        populateCache();

        new ConfigurationUpdater(refreshTimerIntervalInMs, applicationName, configurationDbService,
                configurationCacheService).start();
    }

    /**
     *  Get value belongs to key from cache, if it is not in cache, populates cache from database
     * */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {

        logger.debug("Looking for value from cache.. Key: {}", key);

        T value = configurationCacheService.getValue(applicationName, key);

        if (value != null) {
            logger.debug("Key found at cache.. Key: {},  Value: {}", key, value);
        } else
            logger.info("Key not found at cache! Key: {}", key);

        return value;
    }

    private long getRefreshTimerIntervalInMs() {
        String interval = System.getProperty("dynoconfig.updaterRunIntervalInMs");
        if (interval == null)
            throw new IllegalArgumentException("Updater running interval is empty!");

        return Long.valueOf(System.getProperty("dynoconfig.updaterRunIntervalInMs"));
    }

    private void populateCache() {
        logger.debug("Try to populate cache from database..");
        configurationCacheService.loadToCache(applicationName, configurationDbService.getRecords(applicationName));
        logger.debug("Populated successfully..");
    }
}
