package app;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/**
 * 정보(About) 다이얼로그: 앱 이름·버전·제작자·설명 + GitHub 링크 + 업데이트 확인 버튼.
 * 업데이트 확인은 네트워크 호출이라 {@link SwingWorker}로 EDT를 막지 않고 백그라운드 실행한다.
 */
final class AboutDialog extends JDialog {

    private final JLabel updateResult = new JLabel(" ");
    private final JButton checkButton = new JButton("업데이트 확인");

    AboutDialog(Frame owner) {
        super(owner, "정보", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UiConstants.BG);
        content.setBorder(new EmptyBorder(UiConstants.PAD + 4, UiConstants.PAD + 6,
                UiConstants.PAD, UiConstants.PAD + 6));

        addLeft(content, label(UiConstants.HEADER_TEXT, new Font(UiConstants.FONT_FAMILY, Font.BOLD, 20),
                UiConstants.BTN_FG));
        addLeft(content, Box.createVerticalStrut(2));
        addLeft(content, label("버전 " + UiConstants.appVersion(), UiConstants.SUBTITLE_FONT, UiConstants.TEXT_MUTED));
        addLeft(content, Box.createVerticalStrut(10));
        addLeft(content, label("제작 · " + UiConstants.AUTHOR, UiConstants.GLOBAL_FONT, UiConstants.BTN_FG));
        addLeft(content, Box.createVerticalStrut(4));
        addLeft(content, label(UiConstants.ABOUT_DESC, UiConstants.GLOBAL_FONT, UiConstants.TEXT_MUTED));
        addLeft(content, Box.createVerticalStrut(6));
        addLeft(content, link("GitHub 저장소 열기", UiConstants.GITHUB_URL));
        addLeft(content, Box.createVerticalStrut(16));

        addLeft(content, buildUpdateRow());
        addLeft(content, Box.createVerticalStrut(4));
        updateResult.setFont(UiConstants.SUBTITLE_FONT);
        updateResult.setForeground(UiConstants.TEXT_MUTED);
        addLeft(content, updateResult);

        addLeft(content, Box.createVerticalStrut(16));
        addLeft(content, buildCloseRow());

        setContentPane(content);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(owner);
    }

    private JComponent buildUpdateRow() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        checkButton.setFont(UiConstants.BTN_FONT);
        checkButton.setFocusPainted(false);
        checkButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkButton.addActionListener(e -> runUpdateCheck());
        row.add(checkButton);
        return row;
    }

    private JComponent buildCloseRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JButton close = new JButton("닫기");
        close.setFocusPainted(false);
        close.addActionListener(e -> dispose());
        row.add(close, BorderLayout.EAST);
        return row;
    }

    /** 백그라운드에서 GitHub 조회 → 끝나면 EDT에서 결과 표시. */
    private void runUpdateCheck() {
        checkButton.setEnabled(false);
        updateResult.setForeground(UiConstants.TEXT_MUTED);
        updateResult.setText("확인 중...");
        new SwingWorker<UpdateChecker.Result, Void>() {
            @Override
            protected UpdateChecker.Result doInBackground() {
                return UpdateChecker.check(UiConstants.appVersion());
            }

            @Override
            protected void done() {
                checkButton.setEnabled(true);
                UpdateChecker.Result r;
                try {
                    r = get();
                } catch (Exception ex) {
                    updateResult.setForeground(UiConstants.STATUS_OFF);
                    updateResult.setText("확인 실패: " + ex.getMessage());
                    return;
                }
                updateResult.setText(r.message());
                if (r.ok() && r.updateAvailable()) {
                    updateResult.setForeground(UiConstants.STATUS_OFF);
                    showDownloadLink();
                } else if (r.ok()) {
                    updateResult.setForeground(UiConstants.STATUS_ON);
                } else {
                    updateResult.setForeground(UiConstants.STATUS_OFF);
                }
            }
        }.execute();
    }

    /** 새 버전이 있을 때 결과 라벨 아래에 다운로드 링크를 추가하고 레이아웃을 갱신. */
    private void showDownloadLink() {
        JPanel content = (JPanel) getContentPane();
        JComponent dl = link("다운로드 페이지 열기", UpdateChecker.RELEASES_PAGE);
        dl.setAlignmentX(Component.LEFT_ALIGNMENT);
        int idx = indexOf(content, updateResult) + 1;
        content.add(dl, idx);
        content.revalidate();
        content.repaint();
        pack();
    }

    private static int indexOf(JPanel panel, Component target) {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            if (panel.getComponent(i) == target) {
                return i;
            }
        }
        return panel.getComponentCount() - 1;
    }

    // ---------------- 작은 UI 헬퍼 ----------------

    private static void addLeft(JPanel parent, Component c) {
        if (c instanceof JComponent jc) {
            jc.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        parent.add(c);
    }

    private static JLabel label(String text, Font font, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        return l;
    }

    /** 밑줄 친 클릭 가능한 링크(브라우저로 연다). Desktop 미지원이면 클릭 무시. */
    private JComponent link(String text, String url) {
        JLabel l = new JLabel("<html><u>" + text + "</u></html>");
        l.setFont(UiConstants.GLOBAL_FONT);
        l.setForeground(UiConstants.ACCENT);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openInBrowser(url);
            }
        });
        return l;
    }

    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception ignored) {
            // 브라우저 실행 실패는 무시(핵심 기능 아님)
        }
    }
}
