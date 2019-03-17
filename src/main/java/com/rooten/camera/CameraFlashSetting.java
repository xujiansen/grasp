
package com.rooten.camera;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import lib.grasp.R;

public class CameraFlashSetting extends PopupWindow {
    final static int MENU_WIDTH = 200;
    final static int MENU_HEIGHT = LayoutParams.WRAP_CONTENT;

    private ListView mListview;
    private OnItemClickListener mItemClick;
    private ArrayList<SettingItem> mItems = new ArrayList<>();

    private CameraFlashSetting(View contentView, int width, int height) {
        super(contentView, width, height);
        setOutsideTouchable(true);
        setTouchable(true);
        mListview = (ListView) contentView;
    }

    public static CameraFlashSetting createMenu(Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        ListView listview = (ListView) inflater.inflate(R.layout.camera_setting_list, null);
        return (new CameraFlashSetting(listview, getValueByDpi(ctx, MENU_WIDTH), MENU_HEIGHT));
    }

    public void clearData() {
        mItems.clear();
    }

    public void addItem(Drawable icon, String text) {
        SettingItem item = new SettingItem(icon, text);
        mItems.add(item);
    }

    private static int getValueByDpi(Context ctx, int value) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }

    public void showMenu(View parent) {
        setBackgroundDrawable(new BitmapDrawable());
        SettingAdapter adapter = new SettingAdapter(mListview.getContext(), mItems);
        mListview.setAdapter(adapter);

        if (parent != null) {
            Rect rc = new Rect();
            parent.getGlobalVisibleRect(rc);

            int menuWidth = getValueByDpi(parent.getContext(), MENU_WIDTH);
            int offsetX = rc.left + (rc.width() - menuWidth) / 2;
            showAtLocation(parent, Gravity.LEFT | Gravity.CENTER_VERTICAL, offsetX, 0);
        } else {
            showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mItemClick = l;
    }

    private class SettingItem {
        private Drawable mDraw;
        private String mText;

        public SettingItem(Drawable icon, String text) {
            mDraw = icon;
            mText = text;
        }

        public Drawable getIcon() {
            return mDraw;
        }

        public String getText() {
            return mText;
        }
    }

    private class SettingAdapter extends ArrayAdapter<SettingItem> {
        private final LayoutInflater mInflater;

        public SettingAdapter(Context context, ArrayList<SettingItem> arr) {
            super(context, 0, arr);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final SettingItem item = getItem(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.camera_setting_item, parent, false);
            }

            final View v = convertView;
            final TextView textView = (TextView) convertView;
            textView.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), null, null, null);
            textView.setText(item.getText());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClick != null) {
                        mItemClick.onItemClick(null, v, position, getItemId(position));
                    }
                }
            });
            return convertView;
        }
    }
}
