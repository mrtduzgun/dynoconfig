package com.trendyol.dynoconfig;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Murat Duzgun
 * <p>
 * Unit tests for ConfigurationReader class
 */
public class ConfigurationReaderTest extends AbstractTest {

    @Test
    public void wrongArgumentCheckForInitialization() {

        try {
            new ConfigurationReader(null, dbConnectionString);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Application name and database connection string must be valid!");
        }

        try {
            new ConfigurationReader("SERVICE-A", null);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Application name and database connection string must be valid!");
        }
    }

    @Test
    public void getValueShouldCorrectReturn() {

        setUpdaterRunIntervalInMs();

        ConfigurationReader configurationReader = new ConfigurationReader("SERVICE-A",
                dbConnectionString);

        assertEquals("trendyol.com", configurationReader.getValue("SiteName"));
    }

    @Test
    public void multipleInstanceAtTheSameTime() {

        setUpdaterRunIntervalInMs();

        new ConfigurationReader("SERVICE-A", dbConnectionString);
        new ConfigurationReader("SERVICE-A", dbConnectionString);
    }
}
