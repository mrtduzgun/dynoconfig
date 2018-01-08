package com.trendyol.dynoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Murat Duzgun
 * <p>
 * Provides database connection to get configuration records
 */
public class ConfigurationDbService {
    private final static Logger logger = LoggerFactory.getLogger(ConfigurationDbService.class);

    private final String connectionString;

    public final static String TABLE_NAME = "configuration_record";

    public ConfigurationDbService(String connectionString) {
        this.connectionString = connectionString;
    }

    /**
     *  Get configuration records by application name
     * */
    public List<ConfigurationRecord> getRecords(String appName) {

        logger.debug("Records are fetching from db for application: {}", appName);

        try {
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connect = DriverManager.getConnection(connectionString)) {

                String getRecordSql = "SELECT name, type, value FROM " + TABLE_NAME +
                        " WHERE is_active=true AND app_name='" + appName + "'";

                Statement statement = connect.createStatement();
                try (ResultSet resultSet = statement.executeQuery(getRecordSql)) {

                    List<ConfigurationRecord> configurationRecords = new ArrayList<>();
                    while (resultSet.next()) {
                        configurationRecords.add(new ConfigurationRecord(resultSet.getString("name"),
                                Enum.valueOf(ConfigurationRecord.Type.class, resultSet.getString("type")),
                                resultSet.getString("value")));
                    }

                    return configurationRecords;
                }
            }

        } catch (Exception e) {
            throw new DynoconfigException("An error is occured while records are fetching!", e);
        }
    }
}
