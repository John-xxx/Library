package com.liux.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Liux on 2017/11/8.
 */

public class SingleToast {
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    private static int LAYOUT;
    private static Toast TOAST;

    public static void setLayout(@LayoutRes int resId) {
        LAYOUT = resId;
    }

    public static Toast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getString(resId), duration);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        if (TOAST == null) {
            if (LAYOUT != 0) {
                LayoutInflater inflate = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflate.inflate(LAYOUT, null);

                TOAST = new Toast(context.getApplicationContext());
                TOAST.setView(view);
                TOAST.setText(text);
                TOAST.setDuration(duration);
            } else {
                TOAST = Toast.makeText(context.getApplicationContext(), text, duration);
            }
        } else {
            TOAST.setText(text);
            TOAST.setDuration(duration);
        }
        return TOAST;
    }

    public static void cancel() {
        if (TOAST != null) {
            TOAST.cancel();
            TOAST = null;
        }
    }
}
