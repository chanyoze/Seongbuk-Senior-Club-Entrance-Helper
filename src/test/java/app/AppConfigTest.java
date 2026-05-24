package app;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** AppConfig의 프로그램명 CSV 파싱 로직 단위 테스트. */
class AppConfigTest {

    @Test
    void splitsByComma() {
        assertEquals(
            List.of("노노케어", "스쿨존", "사용자 지정"),
            AppConfig.parsePrograms("노노케어,스쿨존,사용자 지정"));
    }

    @Test
    void trimsWhitespaceAroundCommas() {
        assertEquals(
            List.of("a", "b", "c"),
            AppConfig.parsePrograms("a , b ,c"));
    }

    @Test
    void singleItemYieldsSingletonList() {
        assertEquals(List.of("solo"), AppConfig.parsePrograms("solo"));
    }

    @Test
    void loadReturnsNonEmptyProgramListEndingWithCustom() {
        // config.properties가 있으면 그 값을, 없으면 기본값을 로드 — 둘 다 마지막 항목은 "사용자 지정"
        List<String> names = AppConfig.load().programNames();
        assertFalse(names.isEmpty());
        assertEquals("사용자 지정", names.get(names.size() - 1));
    }
}
