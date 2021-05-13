package ml.withp.gui;

import ml.withp.utility.DateUtils;
import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.JDatePicker;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class LabeledJDatePicker extends JPanel {
    private final JDatePicker picker;

    public LabeledJDatePicker(String label) {
        JDateComponentFactory fac = new JDateComponentFactory();
        picker = fac.createJDatePicker();
        JLabel lbl = new JLabel(label);
        this.add(lbl);
        this.add((Component) picker);
    }

    public Date getDate() {
        return DateUtils.getDate(picker);
    }

    public void setDate(Date d) {
        DateUtils.setDate(picker, d);
    }

}
