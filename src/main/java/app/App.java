package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.util.Enumeration;

public final class App {

    private App() {}

    public static void main(String[] args) {
        applyGlobalFont();
        AppConfig config = AppConfig.load();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(config);
            AutoPasteService autoPaste = AutoPasteService.initialize(frame);
            frame.attachAutoPaste(autoPaste);
            Runtime.getRuntime().addShutdownHook(new Thread(autoPaste::shutdown, "autopaste-shutdown"));
            frame.setVisible(true);
        });
    }

    private static void applyGlobalFont() {
        FontUIResource font = new FontUIResource(UiConstants.GLOBAL_FONT);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }
}
