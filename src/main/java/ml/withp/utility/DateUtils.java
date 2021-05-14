package ml.withp.utility;

import org.jdatepicker.JDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class DateUtils {
    private static String zeroPad(String s) {
        if(s.length() == 1) return "0" + s;
        return s;
    }

    //calendars are weird.
    //first day of month has value 1.
    //first month has value 0.
    public static String formatDay(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        String year = cal.get(Calendar.YEAR) + "-";
        String month = zeroPad(Integer.toString(cal.get(Calendar.MONTH) +1)) + "-";
        String day = zeroPad(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        return year + month + day;
    }

    public static Date getDate(JDatePicker pkr) {
        int day = pkr.getModel().getDay();
        int month = pkr.getModel().getMonth();
        int year = pkr.getModel().getYear();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.clear();
        //noinspection MagicConstant
        cal.set(year,month,day);
        return cal.getTime();
    }

    public static Date addDays(Date d, int count) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        cal.add(Calendar.DATE, count);
        return cal.getTime();
    }

    public static List<Date> getAllDays(Date startDate, Date endDate) {
        Date curDay = startDate;
        List<Date> ret = new ArrayList<>();
        while(curDay.before(endDate) || formatDay(curDay).equals(formatDay(endDate))) {
            ret.add(curDay);
            curDay = addDays(curDay, 1);
        }
        return ret;
    }

    public static Date now() {
        return ldToDate(LocalDate.now());
    }

    public static Date wrongDate() {
        return addDays(ldToDate(LocalDate.now()),-1500);
    }

    private static int getMonth(String monthStr) {
        try {
            Date d = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monthStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return cal.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static int getYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

    public static Date twtToDate(String twtString) {
        if(twtString.contains("hours ago")) return now(); //close enough at least

        String[] parts = twtString.trim().replaceAll(",", "").split(" ");
        int year;

        if(parts.length < 3 )
            year = getYear(now());
        else
            year = Integer.parseInt(parts[2]);

        @SuppressWarnings("MagicConstant") Calendar cal = new GregorianCalendar(year, getMonth(parts[0]), Integer.parseInt(parts[1]));
        return cal.getTime();
    }

    private static Date ldToDate(LocalDate ld) {
        return Date.from(ld.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant());
    }

    public static void setDate(JDatePicker picker, Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        picker.getModel().setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }
}
