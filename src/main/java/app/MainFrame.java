package app;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * 메인 창. LayoutManager(BorderLayout + GridLayout)로 구성해 창 크기·항목 수에 적응한다.
 *  - North : 헤더 배너(제목 + 안내문)
 *  - Center: 프로그램 버튼 그리드(좌) + 미리보기 영역(우)
 *  - South : '사용자 지정' 입력 행
 */
final class MainFrame extends JFrame {

    private AppConfig config;
    private final UsageLog usageLog = new UsageLog();
    private JPanel centerPanel;
    private AutoPasteService autoPaste;
    private JTextArea previewArea;
    private JTextField customField;
    private JLabel hookStatusLabel;
    private JLabel lastCopiedLabel;

    MainFrame(AppConfig config) {
        this.config = config;
        initFrame();
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        setupHotkeys();
    }

    void attachAutoPaste(AutoPasteService service) {
        this.autoPaste = service;
        boolean on = service != null && service.isEnabled();
        hookStatusLabel.setText(on ? "● 자동 붙여넣기 켜짐" : "● 자동 붙여넣기 꺼짐 — 수동 Ctrl+V로 동작");
        hookStatusLabel.setForeground(on ? UiConstants.STATUS_ON : UiConstants.STATUS_OFF);
    }

    /** 미리보기 영역 — 자동 붙여넣기 자체 테스트용으로 노출. */
    JComponent previewArea() {
        return previewArea;
    }

    private void initFrame() {
        setTitle(UiConstants.WINDOW_TITLE);
        setSize(UiConstants.FRAME_WIDTH, UiConstants.FRAME_HEIGHT);
        setMinimumSize(new Dimension(UiConstants.FRAME_MIN_WIDTH, UiConstants.FRAME_MIN_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UiConstants.BG);
    }

    /** 숫자키 1~9로 앞 9개 항목 선택(복사+무장). 단, 입력칸/미리보기에 타이핑 중이면 무시해 숫자 입력을 보존한다. */
    private void setupHotkeys() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) {
                return false;
            }
            Component focus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focus == customField) {
                return false;   // 사용자 지정 입력칸에 타이핑 중일 때만 숫자 양보 (미리보기 등에선 단축키 유지)
            }
            List<String> names = config.programNames();   // 편집으로 바뀔 수 있으니 매번 현재 설정을 읽음
            int gridCount = Math.max(0, names.size() - 1);
            int idx = e.getKeyCode() - KeyEvent.VK_1;   // VK_1..VK_9 → 0..8
            if (idx >= 0 && idx < 9 && idx < gridCount) {
                copyAndArm(names.get(idx));
                return true;   // 소비
            }
            return false;
        });
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiConstants.ACCENT);
        header.setBorder(new EmptyBorder(UiConstants.PAD, UiConstants.PAD + 4, UiConstants.PAD, UiConstants.PAD));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(UiConstants.HEADER_TEXT);
        title.setFont(UiConstants.TITLE_FONT);
        title.setForeground(UiConstants.ON_ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel(UiConstants.HEADER_SUBTITLE);
        subtitle.setFont(UiConstants.SUBTITLE_FONT);
        subtitle.setForeground(UiConstants.HEADER_SUBTITLE_FG);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(UiFactory.ghostButton(UiConstants.icon("ⓘ", "정보"), e -> openAbout()));
        buttons.add(UiFactory.ghostButton(UiConstants.icon("≡", "항목 편집"), e -> openEditor()));
        JPanel eastWrap = new JPanel(new BorderLayout());
        eastWrap.setOpaque(false);
        eastWrap.add(buttons, BorderLayout.NORTH);

        header.add(titles, BorderLayout.WEST);
        header.add(eastWrap, BorderLayout.EAST);
        return header;
    }

    private void openAbout() {
        new AboutDialog(this).setVisible(true);
    }

    private void openEditor() {
        ProgramEditorDialog dlg = new ProgramEditorDialog(this, config.programNames());
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                AppConfig.save(dlg.result());
                reloadPrograms();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "저장에 실패했습니다: " + ex.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** 편집 후: 설정을 다시 읽어 버튼 그리드만 새로 그린다(미리보기·상태바·입력칸은 유지). */
    private void reloadPrograms() {
        config = AppConfig.load();
        BorderLayout bl = (BorderLayout) centerPanel.getLayout();
        Component oldGrid = bl.getLayoutComponent(BorderLayout.CENTER);
        if (oldGrid != null) {
            centerPanel.remove(oldGrid);
        }
        centerPanel.add(buildButtonGrid(), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private JComponent buildCenter() {
        centerPanel = new JPanel(new BorderLayout(UiConstants.GAP, UiConstants.GAP));
        centerPanel.setBackground(UiConstants.BG);
        centerPanel.setBorder(new EmptyBorder(UiConstants.PAD, UiConstants.PAD, UiConstants.PAD, UiConstants.PAD));

        centerPanel.add(buildButtonGrid(), BorderLayout.CENTER);
        centerPanel.add(buildPreview(), BorderLayout.EAST);
        return centerPanel;
    }

    /** 마지막 항목('사용자 지정')을 제외한 프로그램들을 2열 그리드로. (항목 수에 자동 적응) */
    private JComponent buildButtonGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, UiConstants.GAP, UiConstants.GAP));
        grid.setBackground(UiConstants.BG);

        List<String> names = config.programNames();
        int gridCount = Math.max(0, names.size() - 1);
        for (int i = 0; i < gridCount; i++) {
            final String text = names.get(i);
            String label = (i < 9) ? hotkeyLabel(i, text) : text;   // 앞 9개는 ①~⑨ 단축키 배지
            JButton b = makeButton(label, false);
            b.addActionListener(e -> copyAndArm(text));
            grid.add(b);
        }
        return grid;
    }

    private JComponent buildPreview() {
        previewArea = new JTextArea();
        previewArea.setLineWrap(true);
        previewArea.setFont(UiConstants.GLOBAL_FONT);
        previewArea.setOpaque(false);                 // 둥근 흰 카드가 비치도록

        JScrollPane scroll = new JScrollPane(previewArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        JPanel card = UiFactory.card();               // 둥근 흰 배경 카드
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 10, 8, 10));
        card.add(scroll, BorderLayout.CENTER);

        JLabel label = new JLabel(UiConstants.PREVIEW_TITLE);
        label.setForeground(UiConstants.TEXT_MUTED);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(UiConstants.BG);
        wrap.setPreferredSize(new Dimension(UiConstants.PREVIEW_WIDTH, 10));
        wrap.add(label, BorderLayout.NORTH);
        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UiConstants.BG);
        bottom.add(buildCustomRow(), BorderLayout.NORTH);
        bottom.add(buildStatusBar(), BorderLayout.SOUTH);
        return bottom;
    }

    private JComponent buildCustomRow() {
        JPanel row = new JPanel(new BorderLayout(UiConstants.GAP, 0));
        row.setBackground(UiConstants.BG);
        row.setBorder(new EmptyBorder(0, UiConstants.PAD, UiConstants.PAD, UiConstants.PAD));

        List<String> names = config.programNames();
        String customLabel = names.isEmpty() ? "사용자 지정" : names.get(names.size() - 1);

        customField = new JTextField(UiConstants.CUSTOM_FIELD_PLACEHOLDER);
        customField.setFont(UiConstants.GLOBAL_FONT);
        customField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(UiConstants.FIELD_BORDER, UiConstants.BG, UiConstants.FIELD_ARC, 1),
                new EmptyBorder(8, 12, 8, 12)));

        JButton customBtn = makeButton(UiConstants.icon("▶", customLabel), true);
        customBtn.setToolTipText(UiConstants.CUSTOM_BTN_TOOLTIP);
        customBtn.addActionListener(e -> copyAndArm(customField.getText()));

        row.add(customField, BorderLayout.CENTER);
        row.add(customBtn, BorderLayout.EAST);
        return row;
    }

    /** 하단 상태 표시줄: 자동 붙여넣기 상태(좌) + 마지막 복사한 값(우). */
    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(UiConstants.GAP, 0));
        bar.setBackground(UiConstants.STATUS_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiConstants.BTN_BORDER),
                new EmptyBorder(6, UiConstants.PAD, 6, UiConstants.PAD)));

        hookStatusLabel = new JLabel("● 자동 붙여넣기 확인 중...");
        hookStatusLabel.setFont(UiConstants.SUBTITLE_FONT);
        hookStatusLabel.setForeground(UiConstants.TEXT_MUTED);

        lastCopiedLabel = new JLabel(" ");
        lastCopiedLabel.setFont(UiConstants.SUBTITLE_FONT);
        lastCopiedLabel.setForeground(UiConstants.TEXT_MUTED);
        lastCopiedLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        bar.add(hookStatusLabel, BorderLayout.WEST);
        bar.add(lastCopiedLabel, BorderLayout.CENTER);
        return bar;
    }

    private void copyAndArm(String text) {
        ClipboardService.copy(text);
        if (autoPaste != null) {
            autoPaste.arm();
        }
        usageLog.record(text);
        lastCopiedLabel.setText("마지막 복사: " + shorten(text) + " (오늘 " + usageLog.countOf(text) + "회)");
    }

    private static String shorten(String s) {
        if (s == null) return "";
        s = s.replace("\n", " ").trim();
        return s.length() > 20 ? s.substring(0, 20) + "…" : s;
    }

    /** 앞 9개 버튼에 ①~⑨ 원문자 배지(accent 색·굵게)를 붙인 HTML 라벨. */
    private static String hotkeyLabel(int i, String text) {
        char circled = (char) (0x2460 + i);   // ① ② ... ⑨
        return "<html><font color='" + UiConstants.BADGE_HEX + "' size='+1'><b>" + circled
                + "</b></font>&nbsp; " + htmlEscape(text) + "</html>";
    }

    private static String htmlEscape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** 둥근 모서리 버튼 + 호버 효과. accent=true면 강조색(사용자 지정 버튼). */
    private JButton makeButton(String text, boolean accent) {
        return accent ? UiFactory.accentButton(text, null) : UiFactory.neutralButton(text, null);
    }
}
