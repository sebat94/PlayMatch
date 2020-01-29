package com.dam.daniel.playmatch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.dam.daniel.playmatch.MatchActivity;
import com.dam.daniel.playmatch.R;

public class ActivityUtils {

    /**
     * "FLAG_ACTIVITY_CLEAR_TASK" this flag will cause any existing task that would be associated with the activity to be cleared before the activity is started.
     * That is, the activity becomes the new root of an otherwise empty task, and any old activities are finished. This can only be used in conjunction with "FLAG_ACTIVITY_NEW_TASK"
     *
     * @param actualActivity
     * @param nextActivity
     */
    public static void setRoot(Activity actualActivity, Class nextActivity){
        Intent intent = new Intent(actualActivity, nextActivity);
        // When we move to the next Activity, remove MainActivity from the Back Stack
        actualActivity.finish();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Load intent
        actualActivity.getApplicationContext().startActivity(intent);
    }

    /**
     * Push New Activity Above Actual
     *
     * @param actualActivity
     * @param nextActivity
     */
    public static void push(Activity actualActivity, Class nextActivity){
        Intent intent = new Intent(actualActivity, nextActivity);
        actualActivity.getApplicationContext().startActivity(intent);
    }

    // TODO: Call Fragments with optional Bundle

    /**
     *  This method is used to make a fragment overlaps above the "AppBarLayout" that is located in "activity_match" at the same level of the RelativeLayout passed
     *
     * @param relativeLayout view to set the "elevation" property
     * @param overlap True when open fragment, false when exit fragment
     */
    public static void setFragmentOverlapAppBarLayout(RelativeLayout relativeLayout, boolean overlap){
        int elevation;

        if(overlap) elevation = 12; // Minimum amount of elevation needed for superpose this relative layout above the global AppBarLayout(toolbar)
        else elevation = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            relativeLayout.setElevation(elevation);
        }else{
            ViewCompat.setElevation(relativeLayout, elevation);
        }
    }

    /**
     * This method is used to make a fragment overlaps above the "AppBarLayout" that is located in a parent fragment (this occurs when open a fragment inside fragment)
     *
     * @param frameLayout
     * @param overlap
     */
    public static void setFragmentOverlapAppBarLayout(FrameLayout frameLayout, boolean overlap){
        int elevation;

        if(overlap) elevation = 12; // Minimum amount of elevation needed for superpose this relative layout above the global AppBarLayout(toolbar)
        else elevation = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            frameLayout.setElevation(elevation);
        }else{
            ViewCompat.setElevation(frameLayout, elevation);
        }
    }

}
