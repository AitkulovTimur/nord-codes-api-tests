package com.nordcodes.api.config;

import org.aeonbits.owner.Config;

/**
 * Configuration interface for the NordCodes application test suite.
 * <p>
 * This interface defines all configuration properties required for testing,
 * including URLs, ports, authentication credentials, file paths, and timeouts.
 * Configuration is loaded from system properties and the config.properties file.
 * </p>
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
// Если бы были различные env, то перед "classpath:config.properties"
// я бы еще прописал "classpath:${env}.properties"
@Config.Sources({
        "system:properties",
        "classpath:config.properties"
})
public interface GeneralConfig extends Config {
    @Key("http.nordcodes_app_url")
    String nordcodesAppUrl();

    @Key("http.mock_url")
    String mockUrl();

    @Key("ports.mock")
    int mockPort();

    @Key("auth.api_key")
    String apiKey();

    @Key("auth.secret")
    String secret();

    @Key("app.jar_path")
    String jarPath();

    @Key("app.log_directory")
    String logDirectory();

    @Key("timeouts.nordcodes_app_start_seconds")
    long nordcodesAppStartSeconds();

    @Key("timeouts.nordcodes_app_stop_seconds")
    long nordcodesAppStopSeconds();

    @Key("timeouts.socket_connect_milliseconds")
    int socketConnectMilliseconds();

    @Key("timeouts.poll_milliseconds")
    long pollMilliseconds();

    @Key("log.is_detailed_log")
    @DefaultValue("false")
    boolean isDetailedLog();

    @Key("log.is_log_if_validation_fails")
    @DefaultValue("true")
    boolean isLogIfValidationFails();
}
