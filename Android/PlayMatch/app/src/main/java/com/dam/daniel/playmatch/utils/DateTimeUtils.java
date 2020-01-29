package com.dam.daniel.playmatch.utils;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class DateTimeUtils {

    /**
     * Get Time of each Message of a Chat
     * @param dateString
     * @return
     */
    public static String getTimeChatBoxMessage(String dateString){
        Date dt = parseStringToDate(dateString);
        Calendar cal = parseDateToCalendar(dt);
        // Format the Date to Retrieve "0" When integer is only of one digit
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }

    /**
     * Parse String to Date
     * @param dateString
     */
    private static Date parseStringToDate(String dateString){
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date) formatter.parse(dateString);

            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parse Date to Calendar
     * @param date
     * @return
     */
    private static Calendar parseDateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

}
