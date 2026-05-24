package app;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 프로그램 항목 추가/삭제/순서변경 편집 다이얼로그(모달).
 * 마지막 '사용자 지정' 항목은 편집 대상에서 빼고 항상 맨 끝에 유지한다.
 */
final class ProgramEditorDialog extends JDialog {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);
    private final String customSentinel;
    private boolean saved = false;

    ProgramEditorDialog(Frame owner, List<String> fullPrograms) {
        super(owner, "항목 편집", true);

        List<String> editable = new ArrayList<>(fullPrograms);
        customSentinel = editable.isEmpty() ? "사용자 지정" : editable.remove(editable.size() - 1);
        editable.forEach(model::addElement);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(UiConstants.GLOBAL_FONT);
        list.setVisibleRowCount(10);

        JPanel content = new JPanel(new BorderLayout(UiConstants.GAP, UiConstants.GAP));
        content.setBackground(UiConstants.BG);
        content.setBorder(new EmptyBorder(UiConstants.PAD, UiConstants.PAD, UiConstants.PAD, UiConstants.PAD));

        JLabel hint = new JLabel("버튼에 표시할 항목 ('사용자 지정'은 항상 맨 끝에 유지됩니다)");
        hint.setForeground(UiConstants.TEXT_MUTED);
        hint.setBorder(new EmptyBorder(0, 0, UiConstants.GAP, 0));

        content.add(hint, BorderLayout.NORTH);
        content.add(new JScrollPane(list), BorderLayout.CENTER);
        content.add(buildSideButtons(), BorderLayout.EAST);
        content.add(buildBottomButtons(), BorderLayout.SOUTH);

        setContentPane(content);
        setSize(440, 380);
        setLocationRelativeTo(owner);
    }

    boolean isSaved() {
        return saved;
    }

    /** 편집 결과(맨 끝에 '사용자 지정' 유지). */
    List<String> result() {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            out.add(model.get(i));
        }
        out.add(customSentinel);
        return out;
    }

    private JComponent buildSideButtons() {
        JPanel side = new JPanel();
        side.setBackground(UiConstants.BG);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.add(actionButton("＋ 추가", e -> addItem()));
        side.add(Box.createVerticalStrut(6));
        side.add(actionButton("－ 삭제", e -> removeSelected()));
        side.add(Box.createVerticalStrut(6));
        side.add(actionButton("▲ 위로", e -> move(-1)));
        side.add(Box.createVerticalStrut(6));
        side.add(actionButton("▼ 아래로", e -> move(1)));
        return side;
    }

    private JComponent buildBottomButtons() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, UiConstants.GAP, 0));
        bottom.setBackground(UiConstants.BG);

        JButton cancel = new JButton("취소");
        cancel.addActionListener(e -> dispose());

        JButton save = new JButton("저장");
        save.setForeground(UiConstants.CUSTOM_BTN_FG);
        save.setBackground(UiConstants.CUSTOM_BTN_BG);
        save.setOpaque(true);
        save.setFocusPainted(false);
        save.addActionListener(e -> {
            saved = true;
            dispose();
        });

        bottom.add(cancel);
        bottom.add(save);
        return bottom;
    }

    private JButton actionButton(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(UiConstants.BTN_FONT);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(120, 34));
        b.addActionListener(al);
        return b;
    }

    private void addItem() {
        String name = JOptionPane.showInputDialog(this, "추가할 항목 이름:", "항목 추가", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            model.addElement(name.trim());
            list.setSelectedIndex(model.size() - 1);
        }
    }

    private void removeSelected() {
        int i = list.getSelectedIndex();
        if (i >= 0) {
            model.remove(i);
            if (!model.isEmpty()) {
                list.setSelectedIndex(Math.min(i, model.size() - 1));
            }
        }
    }

    private void move(int delta) {
        int i = list.getSelectedIndex();
        int j = i + delta;
        if (i >= 0 && j >= 0 && j < model.size()) {
            String v = model.remove(i);
            model.add(j, v);
            list.setSelectedIndex(j);
        }
    }
}
