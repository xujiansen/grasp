package lib.grasp.widget.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/** 纵横线 */
public class DecorationGrid extends RecyclerView.ItemDecoration {

    private Drawable mDividerDarwable;
    private int mDividerHight = 1;

    public final int[] ATRRS = new int[]{android.R.attr.listDivider};

    public DecorationGrid(Context context) {
        final TypedArray ta = context.obtainStyledAttributes(ATRRS);
        this.mDividerDarwable = ta.getDrawable(0);
        ta.recycle();
    }

    /*
     int dividerHight  分割线的线宽
     Drawable dividerDrawable  图片分割线
     */
    public DecorationGrid(Context context, int dividerHight, Drawable dividerDrawable) {
        this(context);
        mDividerHight = dividerHight;
        mDividerDarwable = dividerDrawable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        //画水平和垂直分割线
        drawHorizontalDivider(c, parent);
        drawVerticalDivider(c, parent);
    }

    public void drawVerticalDivider(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;

            int left = 0;
            int right = 0;

            //左边第一列
            if ((i % 3) == 0) {
                //item左边分割线
                left = child.getLeft();
                right = left + mDividerHight;
                mDividerDarwable.setBounds(left, top, right, bottom);
                mDividerDarwable.draw(c);
                //item右边分割线
//                left = child.getRight() + params.rightMargin - mDividerHight;
//                right = left + mDividerHight;

                left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
                right = left + mDividerHight;
            } else {
                //非左边第一列
                left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
                right = left + mDividerHight;
            }
            //画分割线
            mDividerDarwable.setBounds(left, top, right, bottom);
            mDividerDarwable.draw(c);
        }
    }

    public void drawHorizontalDivider(Canvas c, RecyclerView parent) {

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left  = child.getLeft()  - params.leftMargin - mDividerHight;
            final int right = child.getRight() + params.rightMargin;
            int top     = 0;
            int bottom  = 0;

            // 最上面一行(画两条线)
            if ((i / 3) == 0) {
                //当前item最上面的分割线
                top = child.getTop();
                //当前item下面的分割线
                bottom = top + mDividerHight;
                mDividerDarwable.setBounds(left, top, right, bottom);
                mDividerDarwable.draw(c);
                //item下边分割线
//                top = child.getBottom() + params.bottomMargin;
//                bottom = top + mDividerHight;

                top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                bottom = top + mDividerHight;
            } else {
                //非最上面一行
                top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                bottom = top + mDividerHight;
            }
            //画分割线
            mDividerDarwable.setBounds(left, top, right, bottom);
            mDividerDarwable.draw(c);
        }
    }

    //设置item间距，防止分割线被item覆盖
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

//        if(parent.getChildPosition(view) != 0)
            outRect.left = 1;
            outRect.top = 1;
            outRect.right = 1;
            outRect.bottom = 1;
    }
}
