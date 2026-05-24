package app;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** UsageLog의 CSV 포맷·이스케이프·집계 순수 로직 테스트. */
class UsageLogTest {

    @Test
    void csvEscapePlainText() {
        assertEquals("노노케어", UsageLog.csvEscape("노노케어"));
    }

    @Test
    void csvEscapeQuotesCommaInDoubleQuotes() {
        assertEquals("\"a,b\"", UsageLog.csvEscape("a,b"));
    }

    @Test
    void csvEscapeDoublesInnerQuotes() {
        assertEquals("\"a\"\"b\"", UsageLog.csvEscape("a\"b"));
    }

    @Test
    void formatRowJoinsTimestampAndItem() {
        LocalDateTime t = LocalDateTime.of(2026, 5, 25, 9, 30, 15);
        assertEquals("2026-05-25 09:30:15,노노케어", UsageLog.formatRow(t, "노노케어"));
    }

    @Test
    void roundTripPreservesItemWithCommaAndQuote() {
        String item = "a,b\"c";
        String row = UsageLog.formatRow(LocalDateTime.of(2026, 5, 25, 9, 30, 15), item);
        assertEquals(item, UsageLog.parseItem(row));
    }

    @Test
    void parseItemFromPlainRow() {
        assertEquals("스쿨존", UsageLog.parseItem("2026-05-25 09:30:15,스쿨존"));
    }

    @Test
    void tallyCountsByItem() {
        Map<String, Integer> m = UsageLog.tally(List.of("a", "b", "a", "a"));
        assertEquals(3, m.get("a"));
        assertEquals(1, m.get("b"));
    }
}
