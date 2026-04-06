import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Word-wrap and multi-line draw helpers for UI readability. */
public final class UiText {

    private UiText() {}

    public static List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String trial = line.isEmpty() ? w : line + " " + w;
            if (fm.stringWidth(trial) <= maxWidth) {
                line = new StringBuilder(trial);
            } else {
                if (!line.isEmpty()) {
                    lines.add(line.toString());
                    line = new StringBuilder(w);
                } else {
                    lines.add(w);
                }
            }
        }
        if (!line.isEmpty()) lines.add(line.toString());
        return lines;
    }

    public static void drawWrapped(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight, int maxLines) {
        FontMetrics fm = g2.getFontMetrics();
        List<String> lines = wrap(text, fm, maxWidth);
        int cy = y;
        int n = Math.min(lines.size(), maxLines > 0 ? maxLines : lines.size());
        for (int i = 0; i < n; i++) {
            g2.drawString(lines.get(i), x, cy);
            cy += lineHeight;
        }
    }

    public static int totalHeight(int lineCount, int lineHeight) {
        return Math.max(lineHeight, lineCount * lineHeight);
    }
}
