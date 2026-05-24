package app;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 버튼 클릭으로 클립보드에 텍스트를 넣은 직후 사용자의 다음 좌클릭을 감지해
 * 해당 위치에 Ctrl+V 붙여넣기를 자동으로 수행하고 클립보드를 비운다.
 *
 * JNativeHook 초기화에 실패하면(예: 백신 차단, OS 미지원) 안전하게 비활성화 상태로
 * 동작하여 앱 자체는 평소대로 클립보드 복사 기능까지 정상 동작한다.
 */
final class AutoPasteService {

    private final JFrame appWindow;
    private final AtomicBoolean armed = new AtomicBoolean(false);
    private final AtomicBoolean enabled = new AtomicBoolean(false);

    private AutoPasteService(JFrame appWindow) {
        this.appWindow = appWindow;
    }

    static AutoPasteService initialize(JFrame appWindow) {
        suppressJNativeHookLogging();

        AutoPasteService svc = new AutoPasteService(appWindow);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
                @Override
                public void nativeMousePressed(NativeMouseEvent e) {
                    svc.onMousePressed(e);
                }
                @Override public void nativeMouseClicked(NativeMouseEvent e) {}
                @Override public void nativeMouseReleased(NativeMouseEvent e) {}
            });
            svc.enabled.set(true);
        } catch (Throwable t) {
            // 네이티브 후킹 등록 실패 — 자동 붙여넣기는 비활성화 상태로 두고
            // 앱은 클립보드 복사만 정상 동작하도록 진행한다.
        }
        return svc;
    }

    private static void suppressJNativeHookLogging() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    void arm() {
        if (enabled.get()) {
            armed.set(true);
        }
    }

    boolean isEnabled() {
        return enabled.get();
    }

    void shutdown() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ignored) {
        }
    }

    private void onMousePressed(NativeMouseEvent e) {
        if (e.getButton() != NativeMouseEvent.BUTTON1) return;
        if (!armed.get()) return;
        if (isWithinAppWindow(e.getX(), e.getY())) return;
        if (!armed.compareAndSet(true, false)) return;
        schedulePasteAndClear();
    }

    private boolean isWithinAppWindow(int x, int y) {
        if (!appWindow.isShowing()) return false;
        try {
            Point loc = appWindow.getLocationOnScreen();
            Rectangle bounds = new Rectangle(loc, appWindow.getSize());
            return bounds.contains(x, y);
        } catch (Exception ex) {
            return false;
        }
    }

    private void schedulePasteAndClear() {
        Thread t = new Thread(() -> {
            sleepQuiet(80); // 클릭한 창이 포커스를 받을 시간
            Robot r;
            try {
                r = new Robot();
            } catch (AWTException ex) {
                return;
            }
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_V);
            r.keyRelease(KeyEvent.VK_V);
            r.keyRelease(KeyEvent.VK_CONTROL);
            sleepQuiet(200); // 붙여넣기 완료까지 대기 후 클립보드 비움
            SwingUtilities.invokeLater(ClipboardService::clear);
        }, "auto-paste");
        t.setDaemon(true);
        t.start();
    }

    private static void sleepQuiet(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
