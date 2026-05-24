package app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 항목 복사 기록을 날짜별 CSV(`usage-YYYY-MM-DD.csv`)로 남기고, 항목별 횟수를 집계한다.
 * 파일 I/O 실패는 무시한다(핵심 기능 방해 금지). CSV 포맷/집계 로직은 순수 함수로 분리해 테스트한다.
 */
final class UsageLog {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path dir;
    private final Map<String, Integer> counts = new LinkedHashMap<>();

    UsageLog() {
        this(defaultDir());
    }

    UsageLog(Path dir) {
        this.dir = dir;
        loadToday();
    }

    static Path defaultDir() {
        String appPath = System.getProperty("jpackage.app-path");
        if (appPath != null) {                       // 패키지(exe) 실행 → exe 옆 log/
            Path parent = Paths.get(appPath).getParent();
            if (parent != null) {
                return parent.resolve("log");
            }
        }
        return Paths.get("log");                      // 개발 실행 → 작업 디렉토리(프로젝트 루트)의 log/
    }

    /** 복사 기록: 오늘 CSV에 한 줄 추가 + 메모리 카운트 증가. */
    void record(String item) {
        counts.merge(item, 1, Integer::sum);
        try {
            Files.createDirectories(dir);
            Files.writeString(todayFile(),
                    formatRow(LocalDateTime.now(), item) + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // 기록 실패는 무시
        }
    }

    int countOf(String item) {
        return counts.getOrDefault(item, 0);
    }

    int total() {
        return counts.values().stream().mapToInt(Integer::intValue).sum();
    }

    private Path todayFile() {
        return dir.resolve("usage-" + LocalDate.now() + ".csv");
    }

    /** 오늘 CSV가 있으면 읽어 항목별 카운트를 복원(재시작해도 '오늘 N회' 유지). */
    private void loadToday() {
        try {
            Path file = todayFile();
            if (Files.exists(file)) {
                List<String> items = new ArrayList<>();
                for (String row : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    String item = parseItem(row);
                    if (!item.isEmpty()) {
                        items.add(item);
                    }
                }
                counts.putAll(tally(items));
            }
        } catch (IOException ignored) {
            // 읽기 실패는 무시
        }
    }

    // ---------------- 순수 함수 (단위 테스트 대상) ----------------

    /** CSV 한 줄: "타임스탬프,항목". */
    static String formatRow(LocalDateTime t, String item) {
        return t.format(TS) + "," + csvEscape(item);
    }

    /** RFC4180식 이스케이프: 콤마·따옴표·개행이 있으면 큰따옴표로 감싸고 내부 따옴표는 2개로. */
    static String csvEscape(String s) {
        if (s == null) {
            s = "";
        }
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    /** "타임스탬프,항목" 행에서 항목만 추출(따옴표 처리 해제). 타임스탬프엔 콤마가 없어 첫 콤마로 분리. */
    static String parseItem(String row) {
        if (row == null) {
            return "";
        }
        int comma = row.indexOf(',');
        if (comma < 0) {
            return "";
        }
        return csvUnescape(row.substring(comma + 1).trim());
    }

    static String csvUnescape(String s) {
        s = s.trim();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    /** 항목 리스트 → 항목별 횟수. */
    static Map<String, Integer> tally(List<String> items) {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (String it : items) {
            if (it != null && !it.isBlank()) {
                m.merge(it, 1, Integer::sum);
            }
        }
        return m;
    }
}
