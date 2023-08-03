import javax.swing.*;
import java.awt.*;

//重写List里的Cell
public class CellRenderer extends JLabel implements ListCellRenderer{
    Icon[] icons;

    public CellRenderer(Icon[] icon) {
        this.icons=icon;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        String s=value.toString();
        setText(s);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        if(isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setIcon(icons[index]);
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
