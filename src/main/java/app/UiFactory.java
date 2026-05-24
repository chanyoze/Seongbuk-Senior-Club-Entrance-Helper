package app;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

/** 둥근 버튼·카드 등 공용 UI 컴포넌트 팩토리. 메인 창과 다이얼로그가 같은 스타일(베이비핑크)을 쓰게 한다. */
final class UiFactory {

    private UiFactory() {}

    /** 강조 버튼: 베이비핑크 채움 + 진한 글자(저장·확인 등 주요 동작). */
    static JButton accentButton(String text, ActionListener al) {
        return rounded(text, UiConstants.ACCENT, UiConstants.ACCENT_DARK,
                UiConstants.ON_ACCENT, UiConstants.ACCENT, UiConstants.ARC, 9, 18, al);
    }

    /** 기본 버튼: 흰 배경 + 연분홍 호버 + 핑크 테두리. */
    static JButton neutralButton(String text, ActionListener al) {
        return rounded(text, UiConstants.BTN_BG, UiConstants.BTN_HOVER,
                UiConstants.BTN_FG, UiConstants.BTN_BORDER, UiConstants.ARC, 9, 16, al);
    }

    /** 고스트 버튼: 흰 배경 + 진한 로즈 글자/테두리(밝은 헤더 위에서 또렷). */
    static JButton ghostButton(String text, ActionListener al) {
        return ghostButton(text, UiConstants.ACCENT_DEEP, UiConstants.ACCENT_DARK, al);
    }

    /** 색을 지정하는 고스트 버튼(헤더 버튼마다 다른 포인트 색을 줄 때). */
    static JButton ghostButton(String text, Color fg, Color border, ActionListener al) {
        return rounded(text, Color.WHITE, UiConstants.BTN_HOVER, fg, border, UiConstants.ARC, 7, 16, al);
    }

    /** 둥근 모서리 버튼(롤오버 시 hover 색). 글자/포커스는 기본 UI가 그린다. */
    static JButton rounded(String text, Color bg, Color hover, Color fg, Color border,
                           int arc, int padV, int padH, ActionListener al) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(fg);
        b.setFont(UiConstants.BTN_FONT);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(padV, padH, padV, padH));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (al != null) {
            b.addActionListener(al);
        }
        return b;
    }

    /** 둥근 흰색 카드 패널(연한 테두리). 모서리 바깥은 투명이라 부모 배경이 비친다. */
    static JPanel card() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UiConstants.FIELD_ARC, UiConstants.FIELD_ARC);
                g2.setColor(UiConstants.BTN_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UiConstants.FIELD_ARC, UiConstants.FIELD_ARC);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        return p;
    }
}
