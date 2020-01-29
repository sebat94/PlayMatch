package com.dam.daniel.playmatch.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * The thing about using ObjectAnimator is that it's moving both the visible and the clickable area of the item,
 * if you use another animation method, for example Transition Animation or some other Animators,
 * and let's say if you want to move the Button from the bottom left of the screen to the top left,
 * it will only move the visible area but not the Button itself, the clickable area will still be on the previous position,
 * in this case the clickable area will still be on the bottom left instead of the top left where you moved the button.
 *
 * If you do the same with ObjectAnimator, both the visible area, and the clickable area will move the the desired location.
 */
public class AnimationUtils {

    /**
     * Rotate Animation
     *
     * @param view  Element of view to animate
     * @param orientation   Axis rotation, ex: "rotationX", "rotationY"
     * @param duration  Transition duration
     */
    public static void rotate(View view, String orientation, int duration){
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, orientation, 0.0f, 180f);
        animation.setDuration(duration);
        //animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

}
