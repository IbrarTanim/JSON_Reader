package com.educareapps.jsonreader.utilitis;

import android.app.Activity;
import android.app.Dialog;
import android.view.WindowManager.LayoutParams;

public class DialogNavBarHide {
    public static int BOTTOM = 0;
    public static int LEFT = 2;
    public static int RIGHT = 3;
    public static int TOP = 1;

    public static void navBarHide(Activity activity, Dialog dialog) {
        dialog.getWindow().setFlags(8, 8);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(8);
    }

    public static void dialogAlignment(Dialog dialog, int choice) {
        LayoutParams wlp = dialog.getWindow().getAttributes();
        if (choice == BOTTOM) {
            wlp.gravity = 80;
        } else if (choice == TOP) {
            wlp.gravity = 48;
        } else if (choice == LEFT) {
            wlp.gravity = 3;
        } else if (choice == RIGHT) {
            wlp.gravity = 5;
        }
    }
}
