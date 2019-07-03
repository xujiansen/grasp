package lib.grasp.widget.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/** 区域 */
public class DecorationGap extends RecyclerView.ItemDecoration
{
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST   = LinearLayoutManager.VERTICAL;
    public int              mOrientation;

    public int MIN_HEIGHT = 40;   // 纵向排列时，纵向间隔
    public int MIN_WIDTH  = 20;   // 横向排列时，横向间隔

    /** Margin, 间隔边界 */
    public Paint mPaintMar = new Paint();
    /** Separation, 间隔填充 */
    public Paint mPaintSep = new Paint();

    public DecorationGap(Context context, int orientation)
    {
        setOrientation(orientation);
        initSepration(context);
        initPaint();
    }

    private void initSepration(Context context)
    {
        MIN_HEIGHT  = getValueByDpi(context, 10);
        MIN_WIDTH   = getValueByDpi(context, 10);
    }


    private void initPaint()
    {
        mPaintMar.setColor(Color.parseColor("#c9c9c9"));
        mPaintSep.setColor(Color.parseColor("#00FAFAFA"));

        mPaintMar.setAntiAlias(true);
        mPaintSep.setAntiAlias(true);
    }

    public void setOrientation(int orientation)
    {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST)
        {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent)
    {
        if (mOrientation == VERTICAL_LIST)  drawVertical(c, parent);
        else                                drawHorizontal(c, parent);
    }

    public void drawVertical(Canvas canvas, RecyclerView parent)
    {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int top1 = child.getTop() - MIN_HEIGHT - 1 - params.topMargin - Math.round(ViewCompat.getTranslationY(child));
            final int bottom1 = top1 + MIN_HEIGHT;

            final int top2 = child.getTop() - 1 - params.topMargin - Math.round(ViewCompat.getTranslationY(child));
            final int bottom2 = top2 + 1;

            final int top3 = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
            final int bottom3 = top3 + 1;

            canvas.drawRect(left , top1, right, bottom1, mPaintSep);
            canvas.drawLine(left , top2, right, bottom2, mPaintMar);
            canvas.drawLine(left , top3, right, bottom3, mPaintMar);
        }
    }

    public void drawHorizontal(Canvas canvas, RecyclerView parent)
    {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

//            final int left1 = child.getLeft() - MIN_WIDTH - 1 - params.leftMargin - Math.round(ViewCompat.getTranslationX(child));
//            final int right1 = left1 + MIN_WIDTH;
//
//            final int left2 = right1;
//            final int right2 = left2 + 1;
//
//            final int left3 = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
//            final int right3 = left3 + 1;


            final int point1X = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
            final int point1Y = top;

            final int point2X = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child)) + MIN_WIDTH;
            final int point2Y = (bottom + top) / 2;

            final int point3X = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
            final int point3Y = bottom;

            final int point4X = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child)) + MIN_WIDTH;
            final int point4Y = bottom;

            canvas.drawRect(point1X, point1Y, point4X, point4Y, mPaintSep);
            canvas.drawLine(point1X, point1Y, point2X, point2Y, mPaintMar);
            canvas.drawLine(point2X, point2Y, point3X, point3Y, mPaintMar);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent)
    {
        if (mOrientation == VERTICAL_LIST)  outRect.set(0               , MIN_HEIGHT + 1, 0, 1);
//        else                                outRect.set(MIN_WIDTH + 1   , 0             , 1, 0);
        else                                outRect.set(0   , 0             , MIN_WIDTH, 0);
    }

    public static int getValueByDpi(Context ctx, int value)
    {
        float density = ctx.getResources().getDisplayMetrics().density;
        return (int)(value * density + 0.5f);
    }
}
