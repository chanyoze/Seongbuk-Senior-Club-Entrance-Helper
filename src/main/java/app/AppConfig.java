package app;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

final class AppConfig {
    private static final String CONFIG_FILE = "config.properties";

    private static final String DEFAULT_PROGRAMS =
        "노노케어,스쿨존,도서관도우미,사회서비스형,복지시설도우미,"
        + "일반 취업,고령자 취업,프로그램,탁구,장기·바둑,"
        + "시설 이용,행사 참여,사용자 지정";

    private final List<String> programNames;

    private AppConfig(List<String> programNames) {
        this.programNames = Collections.unmodifiableList(programNames);
    }

    static AppConfig load() {
        Properties props = new Properties();
        Path configPath = resolveConfigPath();
        if (configPath != null && Files.exists(configPath)) {
            try (Reader r = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                props.load(r);
            } catch (IOException ignored) {
                // fall back to defaults
            }
        }

        String programsCsv = props.getProperty("programs", DEFAULT_PROGRAMS);
        List<String> programs = Arrays.asList(programsCsv.split("\\s*,\\s*"));
        return new AppConfig(programs);
    }

    private static Path resolveConfigPath() {
        String appPath = System.getProperty("jpackage.app-path");
        if (appPath != null) {
            Path parent = Paths.get(appPath).getParent();
            if (parent != null) {
                return parent.resolve(CONFIG_FILE);
            }
        }
        return Paths.get(CONFIG_FILE);
    }

    List<String> programNames() {
        return programNames;
    }
}
