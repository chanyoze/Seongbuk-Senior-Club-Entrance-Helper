package app;

import javax.swing.border.AbstractBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * 모서리를 둥글게 그리는 테두리.
 * 컴포넌트의 사각 모서리(둥근 윤곽 바깥)는 {@code outside} 색으로 덮어 잔상 없이 깔끔하게 만든다.
 * 자식이 없는 컴포넌트(JTextField 등)에 적합 — JScrollPane처럼 자식이 위에 그려지는 경우엔 카드 패널을 따로 쓴다.
 */
final class RoundedBorder extends AbstractBorder {

    private final Color line;
    private final Color outside;
    private final int arc;
    private final int thickness;

    RoundedBorder(Color line, Color outside, int arc, int thickness) {
        this.line = line;
        this.outside = outside;
        this.arc = arc;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 둥근 사각형 바깥의 네 모서리를 배경색으로 덮어 사각 잔상 제거
        Area corners = new Area(new Rectangle(x, y, w, h));
        corners.subtract(new Area(new RoundRectangle2D.Float(x, y, w - 1f, h - 1f, arc, arc)));
        g2.setColor(outside);
        g2.fill(corners);

        // 윤곽선
        g2.setColor(line);
        g2.setStroke(new BasicStroke(thickness));
        g2.draw(new RoundRectangle2D.Float(
                x + thickness / 2f, y + thickness / 2f,
                w - 1f - thickness, h - 1f - thickness, arc, arc));
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(thickness, thickness, thickness, thickness);
        return insets;
    }
}
