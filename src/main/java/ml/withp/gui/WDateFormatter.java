package ml.withp.gui;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WDateFormatter extends JFormattedTextField.AbstractFormatter {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN);

    /**
     * Parses <code>text</code> returning an arbitrary Object. Some
     * formatters may return null.
     *
     * @param text String to convert
     * @return Object representation of text
     * @throws ParseException if there is an error in the conversion
     */
    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    /**
     * Returns the string value to display for <code>value</code>.
     *
     * @param value Value to convert
     * @return String representation of value
     * @throws ParseException if there is an error in the conversion
     */
    @Override
    public String valueToString(Object value) throws ParseException {
        if(value == null || ! (value instanceof Calendar)) return "";
        Calendar c = (Calendar) value;
        return dateFormatter.format(c.getTime());
    }
}
