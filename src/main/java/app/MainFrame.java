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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 메인 창. LayoutManager(BorderLayout + GridLayout)로 구성해 창 크기·항목 수에 적응한다.
 *  - North : 헤더 배너(제목 + 안내문)
 *  - Center: 프로그램 버튼 그리드(좌) + 미리보기 영역(우)
 *  - South : '사용자 지정' 입력 행
 */
final class MainFrame extends JFrame {

    private final AppConfig config;
    private AutoPasteService autoPaste;
    private JTextArea previewArea;

    MainFrame(AppConfig config) {
        this.config = config;
        initFrame();
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildSouth(), BorderLayout.SOUTH);
    }

    void attachAutoPaste(AutoPasteService service) {
        this.autoPaste = service;
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

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UiConstants.ACCENT);
        header.setBorder(new EmptyBorder(UiConstants.PAD, UiConstants.PAD + 4, UiConstants.PAD, UiConstants.PAD));

        JLabel title = new JLabel(UiConstants.HEADER_TEXT);
        title.setFont(UiConstants.TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(UiConstants.HEADER_SUBTITLE);
        subtitle.setFont(UiConstants.SUBTITLE_FONT);
        subtitle.setForeground(UiConstants.HEADER_SUBTITLE_FG);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(UiConstants.GAP, UiConstants.GAP));
        center.setBackground(UiConstants.BG);
        center.setBorder(new EmptyBorder(UiConstants.PAD, UiConstants.PAD, UiConstants.PAD, UiConstants.PAD));

        center.add(buildButtonGrid(), BorderLayout.CENTER);
        center.add(buildPreview(), BorderLayout.EAST);
        return center;
    }

    /** 마지막 항목('사용자 지정')을 제외한 프로그램들을 2열 그리드로. (항목 수에 자동 적응) */
    private JComponent buildButtonGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, UiConstants.GAP, UiConstants.GAP));
        grid.setBackground(UiConstants.BG);

        List<String> names = config.programNames();
        int gridCount = Math.max(0, names.size() - 1);
        for (int i = 0; i < gridCount; i++) {
            final String text = names.get(i);
            JButton b = makeButton(text, false);
            b.addActionListener(e -> copyAndArm(text));
            grid.add(b);
        }
        return grid;
    }

    private JComponent buildPreview() {
        previewArea = new JTextArea();
        previewArea.setLineWrap(true);
        previewArea.setFont(UiConstants.GLOBAL_FONT);

        JScrollPane scroll = new JScrollPane(previewArea);
        scroll.setBorder(BorderFactory.createLineBorder(UiConstants.BTN_BORDER));

        JLabel label = new JLabel(UiConstants.PREVIEW_TITLE);
        label.setForeground(UiConstants.TEXT_MUTED);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(UiConstants.BG);
        wrap.setPreferredSize(new Dimension(UiConstants.PREVIEW_WIDTH, 10));
        wrap.add(label, BorderLayout.NORTH);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JComponent buildSouth() {
        JPanel south = new JPanel(new BorderLayout(UiConstants.GAP, 0));
        south.setBackground(UiConstants.BG);
        south.setBorder(new EmptyBorder(0, UiConstants.PAD, UiConstants.PAD, UiConstants.PAD));

        List<String> names = config.programNames();
        String customLabel = names.isEmpty() ? "사용자 지정" : names.get(names.size() - 1);

        JTextField field = new JTextField(UiConstants.CUSTOM_FIELD_PLACEHOLDER);
        field.setFont(UiConstants.GLOBAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiConstants.FIELD_BORDER),
                new EmptyBorder(8, 10, 8, 10)));

        JButton customBtn = makeButton(customLabel, true);
        customBtn.setToolTipText(UiConstants.CUSTOM_BTN_TOOLTIP);
        customBtn.addActionListener(e -> copyAndArm(field.getText()));

        south.add(field, BorderLayout.CENTER);
        south.add(customBtn, BorderLayout.EAST);
        return south;
    }

    private void copyAndArm(String text) {
        ClipboardService.copy(text);
        if (autoPaste != null) {
            autoPaste.arm();
        }
    }

    /** 플랫 스타일 버튼 + 호버 효과. accent=true면 강조색(사용자 지정 버튼). */
    private JButton makeButton(String text, boolean accent) {
        final Color base = accent ? UiConstants.CUSTOM_BTN_BG : UiConstants.BTN_BG;
        final Color hover = accent ? UiConstants.ACCENT_DARK : UiConstants.BTN_HOVER;
        final Color fg = accent ? UiConstants.CUSTOM_BTN_FG : UiConstants.BTN_FG;
        final Color border = accent ? UiConstants.CUSTOM_BTN_BG : UiConstants.BTN_BORDER;

        JButton b = new JButton(text);
        b.setFont(UiConstants.BTN_FONT);
        b.setForeground(fg);
        b.setBackground(base);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                new EmptyBorder(10, 14, 10, 14)));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(base); }
        });
        return b;
    }
}
