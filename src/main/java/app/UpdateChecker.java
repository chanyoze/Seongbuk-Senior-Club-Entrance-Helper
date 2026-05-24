package app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GitHub Releases API로 최신 릴리스 태그를 조회해 현재 버전과 비교한다.
 * - 네트워크 호출({@link #check})은 부수효과가 있어 테스트 제외.
 * - 응답 파싱/버전 비교는 순수 함수로 분리해 단위 테스트({@code UpdateCheckerTest}).
 * JSON 라이브러리 의존을 피하려고 tag_name만 정규식으로 뽑는다(필드 하나라 충분).
 */
final class UpdateChecker {

    static final String API_URL =
            "https://api.github.com/repos/chanyoze/Seongbuk-Senior-Club-Entrance-Helper/releases/latest";
    static final String RELEASES_PAGE =
            "https://github.com/chanyoze/Seongbuk-Senior-Club-Entrance-Helper/releases/latest";

    private static final Pattern TAG = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

    private UpdateChecker() {}

    /** 업데이트 확인 결과. updateAvailable=true면 latestTag가 현재보다 높음. */
    record Result(boolean ok, boolean updateAvailable, String latestTag, String message) {}

    /** 네트워크: 최신 릴리스 조회 → 현재 버전과 비교. 실패해도 예외 대신 ok=false Result 반환. */
    static Result check(String currentVersion) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(8))
                    .build();
            HttpRequest req = HttpRequest.newBuilder(URI.create(API_URL))
                    .header("Accept", "application/vnd.github+json")
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                return new Result(false, false, null, "확인 실패 (HTTP " + resp.statusCode() + ")");
            }
            String tag = parseTagName(resp.body());
            if (tag == null) {
                return new Result(false, false, null, "확인 실패 (응답을 해석할 수 없습니다)");
            }
            boolean update = compareVersions(currentVersion, tag) < 0;
            String msg = update
                    ? "새 버전 " + tag + " 이(가) 있습니다."
                    : "최신 버전을 사용 중입니다. (현재 " + currentVersion + ")";
            return new Result(true, update, tag, msg);
        } catch (Exception e) {
            return new Result(false, false, null, "확인 실패: " + e.getMessage());
        }
    }

    // ---------------- 순수 함수 (단위 테스트 대상) ----------------

    /** GitHub 응답 JSON에서 tag_name 값만 추출. 없으면 null. */
    static String parseTagName(String json) {
        if (json == null) {
            return null;
        }
        Matcher m = TAG.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    /** 태그 정규화: 앞의 v/V 제거. "v1.2.0" → "1.2.0". */
    static String normalize(String tag) {
        if (tag == null) {
            return "";
        }
        String t = tag.trim();
        if (t.startsWith("v") || t.startsWith("V")) {
            t = t.substring(1);
        }
        return t;
    }

    /** semver 비교: a&lt;b → -1, a==b → 0, a&gt;b → 1. 빠진 자리는 0으로 채운다. */
    static int compareVersions(String a, String b) {
        int[] pa = parts(a);
        int[] pb = parts(b);
        int n = Math.max(pa.length, pb.length);
        for (int i = 0; i < n; i++) {
            int x = i < pa.length ? pa[i] : 0;
            int y = i < pb.length ? pb[i] : 0;
            if (x != y) {
                return Integer.compare(x, y);
            }
        }
        return 0;
    }

    private static int[] parts(String v) {
        String s = normalize(v);
        if (s.isEmpty()) {
            return new int[0];
        }
        String[] tokens = s.split("\\.");
        int[] out = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String num = tokens[i].replaceAll("\\D.*$", "");   // "0-rc1" → "0"
            try {
                out[i] = num.isEmpty() ? 0 : Integer.parseInt(num);
            } catch (NumberFormatException e) {
                out[i] = 0;
            }
        }
        return out;
    }
}
