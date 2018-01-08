package com.trendyol.dynoconfig;

/**
 * @author Murat Duzgun
 * <p>
 * Class description here
 */
public class AbstractTest {

    protected final String dbConnectionString = "jdbc:mysql://localhost:3306/dynoconfig" +
            "?verifyServerCertificate=false&useSSL=true&user=root&password=";

    protected void setUpdaterRunIntervalInMs() {
        System.setProperty("dynoconfig.updaterRunIntervalInMs", "30000");
    }
}
