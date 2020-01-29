package com.dam.daniel.playmatch.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import com.dam.daniel.playmatch.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    /**
     * Regular Expression Email
     * @param email
     * @return
     */
    public static boolean regExpEmail(String email){
        Pattern p = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * Regular Expression Password --> 1 Upper, 1 Lower, 1 letter, between 3-32 characters
     * @param password
     * @return
     */
    public static boolean regExpPassword(String password){
        Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{3,32}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * Regular expression Nick --> Can contain numbers, letters, '-', '_', must have between 3 and 14 characters
     * @param nick
     * @return
     */
    public static boolean regExpNick(String nick){
        Pattern p = Pattern.compile("^[a-zA-Z0-9_-]{3,14}$");
        Matcher m = p.matcher(nick);
        return m.matches();
    }

    /**
     * Regular Expression Birthdate --> Valid Formats: 'yyyy/mm/dd' and 'yyyy-mm-dd'
     * @param birthdate
     * @return
     */
    public static boolean regExpBirthdate(String birthdate){
        Pattern p1 = Pattern.compile("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");  // 1994-08-16
        Pattern p2 = Pattern.compile("^\\d{4}\\/(0[1-9]|1[012])\\/(0[1-9]|[12][0-9]|3[01])$");  // 1994/08/16
        Matcher m1 = p1.matcher(birthdate);
        Matcher m2 = p2.matcher(birthdate);
        if(m1.matches() || m2.matches()) return true;
        else return false;
    }

    /**
     * Regular Expression Description --> Any Character And Must have between 0 and 500 characters
     * @param description
     * @return
     */
    public static boolean regExpDescription(String description){
        Pattern p = Pattern.compile(".{0,500}");
        Matcher m = p.matcher(description);
        return m.matches();
    }

    /**
     * Set The Style Error to the input sended
     */
    public static void errorStyleValidation(Context context, EditText actual){
        GradientDrawable gd = new GradientDrawable();
        //gd.setColor(0xFF00FF00); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(5);
        gd.setStroke(5, ContextCompat.getColor(context, R.color.red));
        actual.setBackground(gd);
    }

}
