package com.tugasoft.fintuga.colorchooser;

import android.content.Context;
import android.graphics.Color;

import com.itextpdf.text.pdf.codec.wmf.MetaDo;

public class ColorUtils {
    public static boolean isWhiteText(int i) {
        return (((Color.red(i) * MetaDo.META_PAINTREGION) + (Color.green(i) * 587)) + (Color.blue(i) * 114)) / 1000 < 192;
    }

    public static int getDimensionDp(int i, Context context) {
        return (int) (context.getResources().getDimension(i) / context.getResources().getDisplayMetrics().density);
    }

    public static int dip2px(float f, Context context) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
