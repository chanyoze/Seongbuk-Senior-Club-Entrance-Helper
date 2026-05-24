package app;

import java.awt.Color;
import java.awt.Font;

/** UI 디자인 상수 (색·폰트·문구·간격). 좌표는 LayoutManager가 계산하므로 두지 않는다. */
final class UiConstants {
    private UiConstants() {}

    // 창 / 문구
    static final String WINDOW_TITLE = "출입도우미 v2 made by 이찬호";
    static final String HEADER_TEXT = "출입관리 도우미";
    static final String HEADER_SUBTITLE = "버튼(또는 숫자키 1~9)을 누른 뒤, 입력할 곳을 클릭하면 자동으로 붙여넣습니다";

    // 앱 메타데이터 (정보 다이얼로그 / 업데이트 확인)
    static final String AUTHOR = "이찬호";
    static final String ABOUT_DESC = "성북노인종합복지관 출입 기록용 키워드 복사·자동 붙여넣기 도우미";
    static final String GITHUB_URL = "https://github.com/chanyoze/Seongbuk-Senior-Club-Entrance-Helper";
    /** jar 매니페스트(Implementation-Version)가 없을 때(개발 실행) 쓰는 폴백. build.gradle version과 맞춘다. */
    static final String FALLBACK_VERSION = "1.3.0";

    /** 실행 중인 앱 버전: jar 매니페스트 우선, 없으면(개발 실행) 폴백. */
    static String appVersion() {
        String v = UiConstants.class.getPackage().getImplementationVersion();
        return (v != null && !v.isBlank()) ? v : FALLBACK_VERSION;
    }
    static final int FRAME_WIDTH = 720;
    static final int FRAME_HEIGHT = 640;
    static final int FRAME_MIN_WIDTH = 560;
    static final int FRAME_MIN_HEIGHT = 480;

    // 팔레트
    static final Color ACCENT = new Color(0x2D6CDF);
    static final Color ACCENT_DARK = new Color(0x1F53B8);
    static final Color ACCENT_DEEP = new Color(0x2D6CDF);   // 흰 배경 위 글자(배지·링크·다이얼로그 제목)
    static final Color ON_ACCENT = Color.WHITE;             // accent 면 위 글자색
    static final Color BG = new Color(0xF4F6FA);
    static final Color HEADER_SUBTITLE_FG = new Color(0xDCE7FF);
    static final Color TEXT_MUTED = new Color(0x5B6675);
    static final Color BTN_BG = Color.WHITE;
    static final Color BTN_FG = new Color(0x22303F);
    static final Color BTN_HOVER = new Color(0xEAF0FB);
    static final Color BTN_BORDER = new Color(0xDDE3EE);
    static final Color CUSTOM_BTN_BG = ACCENT;
    static final Color CUSTOM_BTN_FG = ON_ACCENT;
    static final Color FIELD_BORDER = new Color(0xC4CCDA);
    static final Color STATUS_BG = new Color(0xECEFF4);
    static final Color STATUS_ON = new Color(0x1E8E3E);
    static final Color STATUS_OFF = new Color(0xC0392B);
    static final String BADGE_HEX = "#2D6CDF";             // ①~⑨ 단축키 배지 색

    /**
     * 폰트(맑은 고딕)가 표시할 수 있는 글리프만 아이콘으로 붙인다.
     * 표시 불가하면(다른 환경 등) 아이콘을 생략해 두부(□) 깨짐을 원천 차단.
     */
    static String icon(String glyph, String text) {
        return GLOBAL_FONT.canDisplayUpTo(glyph) == -1 ? glyph + "  " + text : text;
    }

    // 폰트
    static final String FONT_FAMILY = "맑은 고딕";
    static final Font GLOBAL_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 22);
    static final Font SUBTITLE_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12);
    static final Font BTN_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);

    // 간격 / 둥글기
    static final int GAP = 10;
    static final int PAD = 16;
    static final int PREVIEW_WIDTH = 240;
    static final int ARC = 18;          // 버튼 모서리 둥글기
    static final int FIELD_ARC = 14;    // 입력칸·미리보기 카드 둥글기

    // 문구
    static final String CUSTOM_FIELD_PLACEHOLDER = "직접 입력...";
    static final String CUSTOM_BTN_TOOLTIP = "왼쪽 입력칸의 내용을 복사합니다";
    static final String PREVIEW_TITLE = "미리보기 (여기에 붙여넣기 테스트)";
}
