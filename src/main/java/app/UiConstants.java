package app;

import java.awt.Color;
import java.awt.Font;

final class UiConstants {
    private UiConstants() {}

    static final String WINDOW_TITLE = "이찬호";
    static final String HEADER_TEXT = "출입관리 도우미";
    static final int FRAME_WIDTH = 700;
    static final int FRAME_HEIGHT = 700;
    static final Color FRAME_BG = Color.LIGHT_GRAY;

    static final String FONT_FAMILY = "굴림";
    static final Font GLOBAL_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 18);

    static final int TITLE_X = 30, TITLE_Y = 10, TITLE_W = 150, TITLE_H = 50;

    static final int BTN_LEFT_X = 30;
    static final int BTN_RIGHT_X = 210;
    static final int BTN_FIRST_Y = 60;
    static final int BTN_W = 150;
    static final int BTN_H = 50;
    static final int BTN_Y_GAP = 80;
    static final int BTN_ROWS_PER_COL = 6;

    static final int CUSTOM_BTN_X = 30;
    static final int CUSTOM_BTN_Y = 540;
    static final int CUSTOM_FIELD_X = 210;
    static final int CUSTOM_FIELD_Y = 540;
    static final int CUSTOM_W = 150;
    static final int CUSTOM_H = 50;

    static final int TEXTAREA_X = 390, TEXTAREA_Y = 60, TEXTAREA_W = 270, TEXTAREA_H = 270;

    static final Color BTN_BG = Color.darkGray;
    static final Color BTN_FG = Color.white;

    static final String CUSTOM_FIELD_PLACEHOLDER = "값을 입력하세요!";
    static final String CUSTOM_BTN_TOOLTIP = "옆에 창에 입력한 내용이 적용됨";
}
