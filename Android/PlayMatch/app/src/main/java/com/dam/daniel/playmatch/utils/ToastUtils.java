package com.dam.daniel.playmatch.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.daniel.playmatch.R;

import static java.util.Objects.isNull;

public class ToastUtils {

    /**
     * Show Toast Error
     * */
    public static void showToastError(Context context, String errorMessage) {
        loadToast(context, errorMessage, getArgbColorAsString(context, R.color.red, null));
    }

    /**
     * Show Toast Information
     * */
    public static void showToastWarning(Context context, String warningMessage) {
        loadToast(context, warningMessage, getArgbColorAsString(context, R.color.amber, null));
    }

    /**
     * Show Toast Information
     * */
    public static void showToastInfo(Context context, String infoMessage) {
        loadToast(context, infoMessage, getArgbColorAsString(context, R.color.colorPrimary, null));
    }

    /**
     * Load Toast
     * */
    private static void loadToast(Context context, String errorMessage, String argb) {
        Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
        View view = toast.getView();
        // Just add something to default styles, don't setBackground() because then @Overrides existing background resource with their color, borders, padding...
        view.getBackground().setColorFilter(Color.parseColor(argb), PorterDuff.Mode.SRC_IN);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        //text.setTextColor(context.getColor(R.color.light_gray));
        text.setTextColor(ContextCompat.getColor(context, R.color.light_gray));
        toast.show();
    }

    /**
     * Set opacity to ARGB obtained (if is null, opacity = 1)
     */
    private static String getArgbColorAsString(Context context, int color, String alpha) {
        return ( "#" + ((alpha == null) ? context.getString(R.string.opacity_100) : alpha) + Integer.toHexString(ContextCompat.getColor(context, color)).substring(2));    // Return Format #ffrrggbb
    }

}
