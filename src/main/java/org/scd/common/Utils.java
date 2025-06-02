package org.scd.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class Utils {

    public static void startConversationWith(Assistant assistant) {
        Logger log = LoggerFactory.getLogger(Assistant.class);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                log.info("==================================================");
                log.info("User: ");
                String userQuery = scanner.nextLine();
                log.info("==================================================");

                if ("exit".equalsIgnoreCase(userQuery)) {
                    break;
                }

                String agentAnswer = assistant.answer(userQuery);
                log.info("==================================================");
                log.info("Assistant: " + agentAnswer);
            }
        }
    }

    public static PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = Utils.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties loadPropertiesByPath(String filePath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
