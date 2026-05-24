package app;

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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/**
 * 정보(About) 다이얼로그: 앱 이름·버전·제작자·설명 + GitHub 링크 + 업데이트 확인.
 * 업데이트 확인은 네트워크 호출이라 {@link SwingWorker}로 EDT를 막지 않고 백그라운드 실행한다.
 */
final class AboutDialog extends JDialog {

    private final JLabel updateResult = new JLabel(" ");
    private final JButton checkButton = UiFactory.accentButton(UiConstants.icon("↻", "업데이트 확인"), null);
    private JComponent downloadLink;   // 새 버전이 있을 때만 추가

    AboutDialog(Frame owner) {
        super(owner, "정보", true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiConstants.BG);
        root.setBorder(new EmptyBorder(UiConstants.PAD + 4, UiConstants.PAD + 6,
                UiConstants.PAD, UiConstants.PAD + 6));

        root.add(buildTitle(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        root.add(buildButtons(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(owner);
    }

    private JComponent buildTitle() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel name = label(UiConstants.HEADER_TEXT, new Font(UiConstants.FONT_FAMILY, Font.BOLD, 22),
                UiConstants.ACCENT_DEEP);
        JLabel ver = label("버전 " + UiConstants.appVersion(), UiConstants.SUBTITLE_FONT, UiConstants.TEXT_MUTED);
        box.add(left(name));
        box.add(Box.createVerticalStrut(2));
        box.add(left(ver));
        return box;
    }

    /** 제작자·설명·GitHub 링크를 담는 둥근 카드 + 업데이트 결과 영역. */
    private JComponent buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel card = UiFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        card.add(left(label("제작 · " + UiConstants.AUTHOR, UiConstants.GLOBAL_FONT, UiConstants.BTN_FG)));
        card.add(Box.createVerticalStrut(6));
        card.add(left(label(UiConstants.ABOUT_DESC, UiConstants.SUBTITLE_FONT, UiConstants.TEXT_MUTED)));
        card.add(Box.createVerticalStrut(10));
        card.add(left(link(UiConstants.icon("↗", "GitHub 저장소 열기"), UiConstants.GITHUB_URL)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        body.add(card);

        body.add(Box.createVerticalStrut(16));
        checkButton.addActionListener(e -> runUpdateCheck());
        body.add(left(checkButton));
        body.add(Box.createVerticalStrut(6));
        updateResult.setFont(UiConstants.SUBTITLE_FONT);
        updateResult.setForeground(UiConstants.TEXT_MUTED);
        body.add(left(updateResult));
        return body;
    }

    private JComponent buildButtons() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(16, 0, 0, 0));
        row.add(UiFactory.neutralButton("닫기", e -> dispose()), BorderLayout.EAST);
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
                } else {
                    updateResult.setForeground(r.ok() ? UiConstants.STATUS_ON : UiConstants.STATUS_OFF);
                }
            }
        }.execute();
    }

    /** 새 버전이 있을 때 결과 라벨 아래에 다운로드 링크를 한 번만 추가. */
    private void showDownloadLink() {
        if (downloadLink != null) {
            return;
        }
        JPanel body = (JPanel) ((JPanel) getContentPane()).getComponent(1);  // CENTER(body)
        downloadLink = link(UiConstants.icon("↓", "다운로드 페이지 열기"), UpdateChecker.RELEASES_PAGE);
        body.add(Box.createVerticalStrut(4));
        body.add(left(downloadLink));
        body.revalidate();
        body.repaint();
        pack();
    }

    // ---------------- 작은 UI 헬퍼 ----------------

    private static Component left(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
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
        l.setForeground(UiConstants.ACCENT_DEEP);
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
