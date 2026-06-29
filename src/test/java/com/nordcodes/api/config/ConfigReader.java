package com.nordcodes.api.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Utility class for reading configuration properties.
 * <p>
 * This class provides a singleton instance of {@link GeneralConfig} that is loaded
 * from the configuration sources defined in the GeneralConfig interface.
 * </p>
 */
public final class ConfigReader {
    public static final GeneralConfig CONFIG = ConfigFactory.create(GeneralConfig.class);

    private ConfigReader() {
    }
}
