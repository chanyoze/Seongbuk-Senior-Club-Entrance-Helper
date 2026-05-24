package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UpdateChecker 순수 함수(응답 파싱·버전 비교) 단위 테스트. 네트워크 호출은 대상 아님. */
class UpdateCheckerTest {

    @Test
    void parseTagName_extractsValue() {
        String json = "{\"url\":\"x\",\"tag_name\":\"V1.2.0\",\"name\":\"release\"}";
        assertEquals("V1.2.0", UpdateChecker.parseTagName(json));
    }

    @Test
    void parseTagName_allowsSpacesAroundColon() {
        assertEquals("v2.0.1", UpdateChecker.parseTagName("{ \"tag_name\" : \"v2.0.1\" }"));
    }

    @Test
    void parseTagName_returnsNullWhenMissing() {
        assertNull(UpdateChecker.parseTagName("{\"name\":\"no tag here\"}"));
        assertNull(UpdateChecker.parseTagName(null));
    }

    @Test
    void normalize_stripsLeadingVOrUpperV() {
        assertEquals("1.2.0", UpdateChecker.normalize("v1.2.0"));
        assertEquals("1.2.0", UpdateChecker.normalize("V1.2.0"));
        assertEquals("1.2.0", UpdateChecker.normalize("1.2.0"));
        assertEquals("", UpdateChecker.normalize(null));
    }

    @Test
    void compareVersions_ordersCorrectly() {
        assertTrue(UpdateChecker.compareVersions("1.2.0", "1.3.0") < 0);
        assertTrue(UpdateChecker.compareVersions("1.3.0", "1.2.0") > 0);
        assertEquals(0, UpdateChecker.compareVersions("1.3.0", "v1.3.0"));
    }

    @Test
    void compareVersions_treatsMissingPartsAsZero() {
        assertEquals(0, UpdateChecker.compareVersions("1.3", "1.3.0"));
        assertTrue(UpdateChecker.compareVersions("1.3", "1.3.1") < 0);
    }

    @Test
    void compareVersions_ignoresSuffixAfterNumber() {
        // "2.0.0-rc1" 같은 접미사는 숫자 부분만 비교
        assertEquals(0, UpdateChecker.compareVersions("2.0.0", "v2.0.0-rc1"));
    }
}
