import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

//colors, fonts and small styled components shared by the whole UI
public final class Theme2 {
    private Theme2() {}

    public static final Color BG      = new Color(0xF2F5F4);
    public static final Color CARD    = Color.WHITE;
    public static final Color INK     = new Color(0x15262E);
    public static final Color ACCENT  = new Color(0x0E8A8C);
    public static final Color ACCENT_HOVER = new Color(0x0A6D6F);
    public static final Color MUTED   = new Color(0x62727A);
    public static final Color BORDER  = new Color(0xD9E1DF);
    public static final Color SUCCESS = new Color(0x2E8B57);
    public static final Color WARNING = new Color(0xD08A1E);
    public static final Color DANGER  = new Color(0xC4452D);
    public static final Color ROW_ALT = new Color(0xF8FAF9);
    public static final Color ROW_SEL = new Color(0xD3EEEE);

    public static Font font(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    public static String money(double value) {
        return String.format(Locale.US, "%.2f EUR", value);
    }

    // ---------- containers ----------

    public static JPanel card() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 17));
        l.setForeground(INK);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.PLAIN, 12));
        l.setForeground(MUTED);
        return l;
    }

    // ---------- buttons ----------

    public static JButton primary(String text)   { return new RoundButton(text, ACCENT, ACCENT_HOVER, Color.WHITE); }
    public static JButton secondary(String text) { return new RoundButton(text, new Color(0xE6ECEA), new Color(0xD5DEDB), INK); }
    public static JButton danger(String text)    { return new RoundButton(text, DANGER, new Color(0xA73823), Color.WHITE); }

    private static class RoundButton extends JButton {
        private static final long serialVersionUID = 1L;

        private final Color base, hover, fg;
        private boolean over;

        RoundButton(String text, Color base, Color hover, Color fg) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.fg = fg;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(font(Font.BOLD, 13));
            setBorder(new EmptyBorder(10, 18, 10, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { over = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { over = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(!isEnabled() ? BORDER : over ? hover : base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            setForeground(isEnabled() ? fg : MUTED);
            super.paintComponent(g);
        }
    }

    // ---------- tables ----------

    public static JTable table(TableModel model) {
        JTable t = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row) ? ROW_SEL : (row % 2 == 0 ? Color.WHITE : ROW_ALT));
                c.setForeground(INK);
                return c;
            }
        };
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFont(font(Font.PLAIN, 13));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 32));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tbl, v, false, false, r, c);
                setBackground(BG);
                setForeground(MUTED);
                setFont(font(Font.BOLD, 12));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(c == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT);
                return this;
            }
        });
        return t;
    }

    public static JScrollPane scroll(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public static TableCellRenderer textRenderer(int align) {
        return new DefaultTableCellRenderer() {
            { setHorizontalAlignment(align); setBorder(new EmptyBorder(0, 10, 0, 10)); }
        };
    }

    public static TableCellRenderer moneyRenderer() {
        return new DefaultTableCellRenderer() {
            { setHorizontalAlignment(SwingConstants.RIGHT); setBorder(new EmptyBorder(0, 10, 0, 10)); }
            @Override protected void setValue(Object v) {
                setText(v instanceof Number ? money(((Number) v).doubleValue()) : String.valueOf(v));
            }
        };
    }
}
