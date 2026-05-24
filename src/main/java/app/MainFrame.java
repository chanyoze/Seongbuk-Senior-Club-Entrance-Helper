package app;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.TextArea;
import java.util.List;

final class MainFrame extends JFrame {

    private final AppConfig config;
    private AutoPasteService autoPaste;
    private TextArea previewArea;

    MainFrame(AppConfig config) {
        this.config = config;
        initFrame();
        addHeader();
        addPreviewArea();
        JTextField customField = addCustomField();
        addProgramButtons(customField);
    }

    void attachAutoPaste(AutoPasteService service) {
        this.autoPaste = service;
    }

    private void initFrame() {
        setTitle(UiConstants.WINDOW_TITLE);
        setSize(UiConstants.FRAME_WIDTH, UiConstants.FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(UiConstants.FRAME_BG);
    }

    private void addHeader() {
        JLabel header = new JLabel(UiConstants.HEADER_TEXT);
        header.setBounds(UiConstants.TITLE_X, UiConstants.TITLE_Y,
                         UiConstants.TITLE_W, UiConstants.TITLE_H);
        header.setHorizontalAlignment(JLabel.CENTER);
        header.setFont(UiConstants.TITLE_FONT);
        getContentPane().add(header);
    }

    private void addPreviewArea() {
        previewArea = new TextArea();
        previewArea.setBounds(UiConstants.TEXTAREA_X, UiConstants.TEXTAREA_Y,
                              UiConstants.TEXTAREA_W, UiConstants.TEXTAREA_H);
        previewArea.append("\n");
        getContentPane().add(previewArea);
    }

    /** 창 안의 미리보기 영역 — 자동 붙여넣기 자체 테스트용으로 노출. */
    TextArea previewArea() {
        return previewArea;
    }

    private JTextField addCustomField() {
        JTextField field = new JTextField(UiConstants.CUSTOM_FIELD_PLACEHOLDER);
        field.setBounds(UiConstants.CUSTOM_FIELD_X, UiConstants.CUSTOM_FIELD_Y,
                        UiConstants.CUSTOM_W, UiConstants.CUSTOM_H);
        field.setHorizontalAlignment(JTextField.CENTER);
        getContentPane().add(field);
        return field;
    }

    private void addProgramButtons(JTextField customField) {
        List<String> names = config.programNames();
        int lastIdx = names.size() - 1;
        for (int i = 0; i < names.size(); i++) {
            JButton btn = createButton(names.get(i));
            placeButton(btn, i, lastIdx);
            attachClipboardListener(btn, names, i, customField);
            getContentPane().add(btn);
        }
    }

    private JButton createButton(String name) {
        JButton b = new JButton(name);
        b.setBackground(UiConstants.BTN_BG);
        b.setForeground(UiConstants.BTN_FG);
        return b;
    }

    private void placeButton(JButton btn, int idx, int lastIdx) {
        if (idx == lastIdx) {
            btn.setBounds(UiConstants.CUSTOM_BTN_X, UiConstants.CUSTOM_BTN_Y,
                          UiConstants.CUSTOM_W, UiConstants.CUSTOM_H);
            btn.setToolTipText(UiConstants.CUSTOM_BTN_TOOLTIP);
            return;
        }
        int col = idx / UiConstants.BTN_ROWS_PER_COL;
        int row = idx % UiConstants.BTN_ROWS_PER_COL;
        int x = (col == 0) ? UiConstants.BTN_LEFT_X : UiConstants.BTN_RIGHT_X;
        int y = UiConstants.BTN_FIRST_Y + row * UiConstants.BTN_Y_GAP;
        btn.setBounds(x, y, UiConstants.BTN_W, UiConstants.BTN_H);
    }

    private void attachClipboardListener(JButton btn, List<String> names, int idx, JTextField customField) {
        final boolean isCustom = (idx == names.size() - 1);
        final String fixedText = isCustom ? null : names.get(idx);
        btn.addActionListener(e -> {
            String text = isCustom ? customField.getText() : fixedText;
            ClipboardService.copy(text);
            if (autoPaste != null) {
                autoPaste.arm();
            }
        });
    }
}
