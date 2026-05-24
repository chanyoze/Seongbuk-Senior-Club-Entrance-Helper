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
        return new AppConfig(parsePrograms(programsCsv));
    }

    /** "a, b ,c" 형식 CSV를 콤마(주변 공백 허용) 기준으로 분리. 파일 의존 없이 테스트하려고 분리한 순수 함수. */
    static List<String> parsePrograms(String csv) {
        return Arrays.asList(csv.split("\\s*,\\s*"));
    }

    /** programs 목록을 config.properties에 저장(평문 UTF-8). load()와 같은 위치에 쓴다. */
    static void save(List<String> programs) throws IOException {
        String content = "# 버튼에 표시할 프로그램 이름 목록 (콤마로 구분)" + System.lineSeparator()
                + "# 마지막 항목은 우측 텍스트필드에 입력된 값을 복사하는 '사용자 지정' 버튼이 됩니다." + System.lineSeparator()
                + "programs=" + String.join(",", programs) + System.lineSeparator();
        Files.writeString(resolveConfigPath(), content, StandardCharsets.UTF_8);
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
