package app;

import java.awt.Color;
import java.awt.Font;

/** UI 디자인 상수 (색·폰트·문구·간격). 좌표는 LayoutManager가 계산하므로 두지 않는다. */
final class UiConstants {
    private UiConstants() {}

    // 창 / 문구
    static final String WINDOW_TITLE = "출입도우미 v2 made by 이찬호";
    static final String HEADER_TEXT = "출입관리 도우미";
    static final String HEADER_SUBTITLE = "버튼을 누른 뒤, 입력할 곳을 클릭하면 자동으로 붙여넣습니다";
    static final int FRAME_WIDTH = 720;
    static final int FRAME_HEIGHT = 640;
    static final int FRAME_MIN_WIDTH = 560;
    static final int FRAME_MIN_HEIGHT = 480;

    // 팔레트
    static final Color ACCENT = new Color(0x2D6CDF);
    static final Color ACCENT_DARK = new Color(0x1F53B8);
    static final Color BG = new Color(0xF4F6FA);
    static final Color HEADER_SUBTITLE_FG = new Color(0xDCE7FF);
    static final Color TEXT_MUTED = new Color(0x5B6675);
    static final Color BTN_BG = Color.WHITE;
    static final Color BTN_FG = new Color(0x22303F);
    static final Color BTN_HOVER = new Color(0xEAF0FB);
    static final Color BTN_BORDER = new Color(0xD3DAE6);
    static final Color CUSTOM_BTN_BG = ACCENT;
    static final Color CUSTOM_BTN_FG = Color.WHITE;
    static final Color FIELD_BORDER = new Color(0xC4CCDA);

    // 폰트
    static final String FONT_FAMILY = "맑은 고딕";
    static final Font GLOBAL_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 22);
    static final Font SUBTITLE_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12);
    static final Font BTN_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);

    // 간격
    static final int GAP = 10;
    static final int PAD = 16;
    static final int PREVIEW_WIDTH = 240;

    // 문구
    static final String CUSTOM_FIELD_PLACEHOLDER = "직접 입력...";
    static final String CUSTOM_BTN_TOOLTIP = "왼쪽 입력칸의 내용을 복사합니다";
    static final String PREVIEW_TITLE = "미리보기 (여기에 붙여넣기 테스트)";
}
