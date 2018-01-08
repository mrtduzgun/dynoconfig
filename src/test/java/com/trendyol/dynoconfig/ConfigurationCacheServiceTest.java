package com.trendyol.dynoconfig;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Murat Duzgun
 * <p>
 * Unit tests for ConfigurationCacheService class
 */
public class ConfigurationCacheServiceTest extends AbstractTest {

    @Test
    public void isCorrectLoadToCache() {

        List<ConfigurationRecord> configurationRecordList = Arrays.asList(
                new ConfigurationRecord("config1", ConfigurationRecord.Type.INTEGER, "123"),
                new ConfigurationRecord("config2", ConfigurationRecord.Type.BOOLEAN, "0")
        );

        new ConfigurationCacheService().loadToCache("SERVICE-A", configurationRecordList);

        assertEquals("INTEGER_123", new Jedis().get("SERVICE-A_config1"));
        assertEquals("BOOLEAN_0", new Jedis().get("SERVICE-A_config2"));
    }
}
