package app;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

final class ClipboardService {
    private ClipboardService() {}

    static void copy(String text) {
        setContents(text);
    }

    private static void setContents(String text) {
        StringSelection data = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(data, null);
    }
}
