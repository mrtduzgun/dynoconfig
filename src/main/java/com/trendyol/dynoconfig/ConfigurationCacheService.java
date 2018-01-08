package com.trendyol.dynoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * @author Murat Duzgun
 * <p>
 * Read and load to records to cache, we store key value pair like 'SERVICE-A_SiteName => INTEGER_12'
 */
public class ConfigurationCacheService {
    private final static Logger logger = LoggerFactory.getLogger(ConfigurationCacheService.class);

    private final JedisPool jedisPool;

    public ConfigurationCacheService() {
        this.jedisPool = new JedisPool(buildPoolConfig(), "localhost");
    }

    /**
     * Populate cache from database
     **/
    public void loadToCache(String appName, List<ConfigurationRecord> configurationRecordList) {

        try (Jedis jedis = jedisPool.getResource()) {

            configurationRecordList.forEach(record ->
                    jedis.set(generateCacheKey(appName, record.getName()),
                            generateValueWithType(record.getType(), record.getValue())));

            Set<String> keys = jedis.keys(appName + "*");

            String foundKey;
            for (String key : keys) {

                foundKey = null;
                for (ConfigurationRecord record : configurationRecordList) {
                    if (key.equals(generateCacheKey(appName, record.getName()))) {
                       foundKey = key;
                    }
                }

                if (foundKey == null)
                    jedis.del(key);
            }
        }

        logger.debug("Db records is loaded to cache..");
    }

    /**
     *  Extract value with specified key and try to convert to desired type
     * */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String appName, String name) {

        try (Jedis jedis = jedisPool.getResource()) {
            String rawValue = jedis.get(generateCacheKey(appName, name));

            if (rawValue == null)
                return null;

            String[] pieces = rawValue.split("_");

            if (pieces.length == 2) {

                ConfigurationRecord.Type storedType = Enum.valueOf(ConfigurationRecord.Type.class, pieces[0]);
                Object storedValue;

                try {
                    if (ConfigurationRecord.Type.INTEGER == storedType) {
                        storedValue = Integer.valueOf(pieces[1]);

                    } else if (ConfigurationRecord.Type.STRING == storedType) {
                        storedValue = pieces[1];

                    } else if (ConfigurationRecord.Type.DOUBLE == storedType) {
                        storedValue = Double.valueOf(pieces[1]);

                    } else if (ConfigurationRecord.Type.BOOLEAN == storedType) {
                        storedValue = "1".equals(pieces[1]) ? Boolean.TRUE : Boolean.FALSE;

                    } else {
                        throw new DynoconfigException("Found configuration type is invalid!");
                    }

                    return (T)storedValue;

                } catch (NumberFormatException e) {
                    throw new DynoconfigException("Configuration value type did not matched value! Found value " +
                            pieces[1] + " and type:" + pieces[0]);
                }
            } else
                throw new DynoconfigException("Stored cache value is invalid!");
        }
    }

    /**
     *  Pooling for jedis instances for multi threaded environments
     * */
    private JedisPoolConfig buildPoolConfig() {

        final JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);

        return poolConfig;
    }

    /**
     *  String representation of cache key eg. SERVICE-A_SiteName
     * */
    private String generateCacheKey(String appName, String name) {
        if (appName != null && name != null)
            return appName + "_" + name;
        return null;
    }

    /**
     *  String representation of stored value eg. INTEGER_12 (Type: INTEGER, Value: 12)
     * */
    private String generateValueWithType(ConfigurationRecord.Type type, String value) {
        return type.name() + "_" + value;
    }
}
