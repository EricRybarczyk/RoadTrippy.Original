package ericrybarczyk.me.roadtrippy.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * This class and the techniques used to incorporate FontAwesome are from the tutorial at
 * https://code.tutsplus.com/tutorials/how-to-use-fontawesome-in-an-android-app--cms-24167
 * written by Gianluca Segato, https://tutsplus.com/authors/gianluca-segato
 */

public class FontManager {
    private static final String ROOT = "fonts/";
    public static final String FONTAWESOME_SOLID = ROOT + "fa-solid-900.ttf";
    public static final String FONTAWESOME_REGULAR = ROOT + "fa-regular-400.ttf";

    public static Typeface getTypeface(Context context, String fontName) {
        return Typeface.createFromAsset(context.getAssets(), fontName);
    }
}