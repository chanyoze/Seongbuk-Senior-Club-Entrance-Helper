package app;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import javax.swing.JFrame;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 버튼 클릭으로 클립보드에 텍스트를 넣은 직후 사용자의 다음 좌클릭을 감지해
 * 해당 위치에 Ctrl+V 붙여넣기를 자동으로 수행한다.
 *
 * JNativeHook 초기화에 실패하면(예: 백신 차단, OS 미지원) 안전하게 비활성화 상태로
 * 동작하여 앱 자체는 평소대로 클립보드 복사 기능까지 정상 동작한다.
 */
final class AutoPasteService {

    private final JFrame appWindow;
    private final Component inWindowTarget;   // 창 안이어도 붙여넣기를 허용할 컴포넌트(미리보기 영역). null이면 모든 창 안 클릭 무시.
    private final AtomicBoolean armed = new AtomicBoolean(false);
    private final AtomicBoolean enabled = new AtomicBoolean(false);

    private AutoPasteService(JFrame appWindow, Component inWindowTarget) {
        this.appWindow = appWindow;
        this.inWindowTarget = inWindowTarget;
    }

    static AutoPasteService initialize(JFrame appWindow, Component inWindowTarget) {
        suppressJNativeHookLogging();

        AutoPasteService svc = new AutoPasteService(appWindow, inWindowTarget);
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
        // 창 밖이면 OK. 창 안이면 미리보기 영역 위 클릭만 허용(자체 테스트용), 그 외 창 안 클릭은 무시.
        if (isWithinAppWindow(e.getX(), e.getY()) && !isOnComponent(inWindowTarget, e.getX(), e.getY())) return;
        if (!armed.compareAndSet(true, false)) return;
        schedulePaste();
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

    private static boolean isOnComponent(Component c, int x, int y) {
        if (c == null || !c.isShowing()) return false;
        try {
            Point loc = c.getLocationOnScreen();
            Rectangle bounds = new Rectangle(loc, c.getSize());
            return bounds.contains(x, y);
        } catch (Exception ex) {
            return false;
        }
    }

    private void schedulePaste() {
        Thread t = new Thread(() -> {
            // 클릭한 창이 포커스를 확실히 받도록 대기. 80ms는 너무 짧아 간헐적으로 실패 → 200ms로 늘림.
            sleepQuiet(200);
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
            // 클립보드 비우기 제거: 붙여넣기가 끝나기 전에 비워져 빈 값이 붙던 레이스를 없애 '무조건 붙여넣기'가 되게 함.
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
