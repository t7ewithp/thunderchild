package ml.withp.gui;

import javax.swing.*;
import java.awt.*;

public class LabeledTextField extends JPanel {
    private JLabel lbl;
    private JTextField fld;
    private static final Dimension SIZE = new Dimension(250,16);

    public LabeledTextField(String label, String initialTxt) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        lbl = new JLabel(label);
        fld = new JTextField(initialTxt);
        fld.setPreferredSize(SIZE);
        fld.setMinimumSize(SIZE);
        this.add(lbl);
        this.add(fld);
    }

    public String getFieldText() {
        return fld.getText();
    }

    public void setFieldText(String txt) {
        fld.setText(txt);
    }

    public LabeledTextField(String label) {
        this(label, "");
    }
}
