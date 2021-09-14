package application;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.border.AbstractBorder;

@SuppressWarnings("serial")
public class RoundedLineBorder extends AbstractBorder {
    int lineSize, cornerSize;
    Paint fill;
    Stroke stroke;
    private Object aaHint;

    public RoundedLineBorder(Paint fill, int lineSize, int cornerSize) {
        this.fill = fill;
        this.lineSize = lineSize;
        this.cornerSize = cornerSize;
        stroke = new BasicStroke(lineSize);
    }
    
    public RoundedLineBorder(Paint fill, int lineSize, int cornerSize, boolean antiAlias) {
        this.fill = fill;
        this.lineSize = lineSize;
        this.cornerSize = cornerSize;
        stroke = new BasicStroke(lineSize);
        aaHint = antiAlias? RenderingHints.VALUE_ANTIALIAS_ON: RenderingHints.VALUE_ANTIALIAS_OFF;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        int size = Math.max(lineSize, cornerSize);
        if(insets == null) insets = new Insets(size, size, size, size);
        else insets.left = insets.top = insets.right = insets.bottom = size;
        return insets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D)g;
        Paint oldPaint = g2d.getPaint();
        Stroke oldStroke = g2d.getStroke();
        Object oldAA = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        try {
            g2d.setPaint(fill!=null? fill: c.getForeground());
            g2d.setStroke(stroke);
            if(aaHint != null) g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aaHint);
            int off = lineSize >> 1;
            g2d.drawRoundRect(x+off, y+off, width-lineSize, height-lineSize, cornerSize, cornerSize);
        }
        finally {
            g2d.setPaint(oldPaint);
            g2d.setStroke(oldStroke);
            if(aaHint != null) g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
        }
    }
}