package com.nordcodes.api.support;

import com.nordcodes.api.exception.NordCodesAppStartException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.nordcodes.api.config.ConfigReader.CONFIG;
import static org.awaitility.Awaitility.await;

/**
 * Manages the lifecycle of the NordCodes application process.
 * <p>
 * This class provides functionality to start, stop, and monitor the NordCodes application
 * during test execution. It handles process management, port availability checking,
 * and log file configuration.
 * </p>
 */
public class NordCodesAppRunner {
    private static final String JAVA_HOME_PROPERTY_NAME = "java.home";
    private static final String JAVA_UNIX_CMD = "java";
    private static final String JAVA_WIN_CMD = "java.exe";
    private static final String OS_NAME_PROPERTY_NAME = "os.name";
    private static final String JAR_OPTION = "-jar";
    private static final DateTimeFormatter LOG_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private final String testClassName;
    private Process nordCodesAppProcess;

    public NordCodesAppRunner(String testClassName) {
        this.testClassName = testClassName;
    }

    /**
     * Starts the NordCodes application process.
     * <p>
     * If the application is already running, this method returns immediately.
     * Otherwise, it configures and starts the process, then waits for the application
     * to become available on its configured port. If startup fails, it attempts to
     * stop the process and throws an exception.
     * </p>
     *
     * @throws NordCodesAppStartException if the application fails to start
     */
    public void start() {
        if (nordCodesAppProcess != null && nordCodesAppProcess.isAlive()) {
            return;
        }

        try {
            ProcessBuilder processBuilder = configureProcessBuilder();
            nordCodesAppProcess = processBuilder.start();
            waitUntilApplicationStarted();
        } catch (Exception e) {
            stop();
            throw new NordCodesAppStartException(e);
        }
    }

    private void waitUntilApplicationStarted() {
        await()
                .atMost(Duration.ofSeconds(CONFIG.nordcodesAppStartSeconds()))
                .pollInterval(Duration.ofMillis(CONFIG.pollMilliseconds()))
                .until(this::isNordCodesAppStarted);
    }

    /**
     * Checks if the NordCodes application has started successfully.
     * <p>
     * This method verifies that the process is alive and that the application
     * port is open and accepting connections.
     * </p>
     *
     * @return true if the application is running and the port is open, false otherwise
     */
    public boolean isNordCodesAppStarted() {
        return nordCodesAppProcess != null
                && nordCodesAppProcess.isAlive()
                && isNordCodesAppPortOpen();
    }

    private boolean isNordCodesAppPortOpen() {
        URI uri = URI.create(CONFIG.nordcodesAppUrl());

        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(uri.getHost(), uri.getPort()),
                    CONFIG.socketConnectMilliseconds()
            );

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private ProcessBuilder configureProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder(
                getJavaExecutablePath(),
                "-Dsecret=" + CONFIG.secret(),
                String.format("-Dmock=%s/", CONFIG.mockUrl()),
                JAR_OPTION,
                CONFIG.jarPath()
        );

        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.to(getApplicationLogFile()));

        return processBuilder;
    }

    private String getJavaExecutablePath() {
        String javaHome = System.getProperty(JAVA_HOME_PROPERTY_NAME);
        String executableName = isWindows() ? JAVA_WIN_CMD : JAVA_UNIX_CMD;

        return Path.of(javaHome, "bin", executableName).toString();
    }

    private boolean isWindows() {
        return System.getProperty(OS_NAME_PROPERTY_NAME)
                .toLowerCase(Locale.ROOT)
                .contains("win");
    }

    private File getApplicationLogFile() {
        String timestamp = LocalDateTime.now().format(LOG_TIMESTAMP_FORMATTER);

        Path logFilePath = Path.of(
                CONFIG.logDirectory(),
                "%s_%s.log".formatted(testClassName, timestamp)
        );

        try {
            Files.createDirectories(logFilePath.getParent());
        } catch (IOException e) {
            throw new NordCodesAppStartException(
                    "Failed to create directory for application logs: " + logFilePath.getParent(),
                    e
            );
        }

        return logFilePath.toFile();
    }

    /**
     * Stops the NordCodes application process.
     * <p>
     * If the process is not running, this method returns immediately.
     * Otherwise, it attempts to stop the process gracefully. If the process does not
     * terminate within the configured timeout, it is forcibly terminated.
     * </p>
     */
    public void stop() {
        if (nordCodesAppProcess == null || !nordCodesAppProcess.isAlive()) {
            return;
        }

        nordCodesAppProcess.destroy();

        try {
            boolean stopped = nordCodesAppProcess.waitFor(
                    CONFIG.nordcodesAppStopSeconds(),
                    TimeUnit.SECONDS
            );

            if (!stopped) {
                nordCodesAppProcess.destroyForcibly();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            nordCodesAppProcess.destroyForcibly();
        }
    }
}
