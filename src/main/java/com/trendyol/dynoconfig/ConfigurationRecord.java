package com.trendyol.dynoconfig;

/**
 * @author Murat Duzgun
 * <p>
 * Holds each configuration record
 */
public class ConfigurationRecord {

    /**
     *  Available types for values
     * */
    public enum Type {
        INTEGER,
        STRING,
        DOUBLE,
        BOOLEAN
    }

    private String name;

    private Type type;

    private String value;

    public ConfigurationRecord(String name, Type type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
