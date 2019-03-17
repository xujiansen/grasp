package com.rooten.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;

import com.rooten.Constant;

public class IconifyUtil {
    public static IconDrawable getIcon(Context cxt, String key) {
        try {
            return new IconDrawable(cxt, key);
        } catch (Exception e) {
            return null;
        }
    }

    public static IconDrawable getIconByColor(Context cxt, String key, int color, int sizeDp) {
        IconDrawable d = getIcon(cxt, key);
        if (d == null) return null;
        d.color(color);
        d.sizeDp(sizeDp);
        return d;
    }

    public static IconDrawable getIconByColorRes(Context cxt, String key, int colorRes, int sizeDp) {
        IconDrawable d = getIcon(cxt, key);
        if (d == null) return null;
        d.colorRes(colorRes);
        d.sizeDp(sizeDp);
        return d;
    }

    public static void setIconByColor(ImageView imageview, String key, int color, int sizeDp) {
        if (imageview == null) return;
        Context cxt = imageview.getContext();
        Drawable d = getIconByColor(cxt, key, color, sizeDp);
        if (d == null) return;
        imageview.setImageDrawable(d);
    }

    public static void setIconByColorRes(ImageView imageview, String key, int colorRes, int sizeDp) {
        if (imageview == null) return;
        Context cxt = imageview.getContext();
        Drawable d = getIconByColorRes(cxt, key, colorRes, sizeDp);
        if (d == null) return;
        imageview.setImageDrawable(d);
    }

    public static void setRightArrow(ImageView imageview, int color) {
        setIconByColor(imageview, "mdi-chevron-right", color, 22);
    }

    public static void setRightArrow(ImageView imageview) {
        setIconByColor(imageview, "mdi-chevron-right", Color.parseColor("#939393"), 22);
    }

    public static void setDownArrow(ImageView imageview) {
        setIconByColor(imageview, "mdi-chevron-down", Color.parseColor("#939393"), 22);
    }

    public static void setMenuAddIcon(Context context, MenuItem item) {
        IconDrawable d = getIcon(context, "md-add");
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        item.setIcon(d);
    }

    public static void setMenuPrintIcon(Context context, MenuItem item) {
        IconDrawable d = getIcon(context, "md-print");
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        item.setIcon(d);
    }

    public static void setMenuSubmitIcon(Context context, MenuItem item) {
        IconDrawable d = getIcon(context, "md-done");
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        item.setIcon(d);
    }

    public static void setMenuErrorIcon(Context context, MenuItem item) {
        IconDrawable d = getIcon(context, "md-error");
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        item.setIcon(d);
    }

    public static void setMenuQueryIcon(Context context, MenuItem item) {
        IconDrawable d = getIcon(context, "mdi-magnify");
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        item.setIcon(d);
    }

    public static void setMenuSettingIcon(Context context, MenuItem item) {
        IconDrawable d = getIcon(context, "md-settings");
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        item.setIcon(d);
    }

    public static void setMenuItemIcon(Context context, MenuItem item, String icon) {
        setMenuItemIcon(context, item, Constant.COLOR_TOOLBAR, icon);
    }

    public static void setMenuItemIcon(Context context, MenuItem item, int color, String icon) {
        IconDrawable d = getIcon(context, icon);
        if (d == null) return;

        d.color(color);
        d.actionBarSize();
        item.setIcon(d);
    }
}
