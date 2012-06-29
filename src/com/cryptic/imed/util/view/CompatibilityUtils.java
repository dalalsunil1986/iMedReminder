package com.cryptic.imed.util.view;

import android.app.Activity;
import android.os.Build;

/**
 * @author sharafat
 */
public class CompatibilityUtils {
    public static void setHomeButtonEnabled(boolean homeButtonEnabled, Activity activity) {
        if (Build.VERSION.SDK_INT >= 14) {
            activity.getActionBar().setHomeButtonEnabled(true);
        }
    }
}
