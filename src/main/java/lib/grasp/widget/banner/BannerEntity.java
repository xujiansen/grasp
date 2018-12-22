package lib.grasp.widget.banner;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.rooten.ctrl.widget.RoundImageView;
import lib.grasp.R;
import lib.grasp.util.GlideUtils;

/**
 * Created by GaQu_Dev on 2018/9/26.
 */
public class BannerEntity {
    /** 展示画面 */
    public View mView;
    /** 展示文字 */
    public String title;
    /** 点击跳转url */
    public String url;
    /** 指示器 */
    public IconTextView itv;

    public BannerEntity(Context ctx, View view, String title, String url, View.OnClickListener listener) {
        mView = View.inflate(ctx, R.layout.banner_item, null);
        LinearLayout container = mView.findViewById(R.id.container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container.addView(view, params);
        mView.setTag(this);

        this.title = title;
        this.url = url;
        this.itv = getInitedItv(ctx);

        setListener(listener);
    }

    /** 设置点击监听,url从View的tag中获取 */
    private void setListener(View.OnClickListener listener){
        if(listener != null) mView.setOnClickListener(listener);
    }

    /** 获取初始化的指示器 */
    private IconTextView getInitedItv(Context context){
        IconTextView itv = new IconTextView(context);
        itv.setGravity(Gravity.CENTER);
        itv.setText("{fa-circle}");
        itv.setTextSize(10);
        itv.setAlpha(0.5f);
        itv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        return itv;
    }

    /** 获取一项指示器 */
    public static RoundImageView getIndicator(Context ctx){
        RoundImageView imageView = new RoundImageView(ctx);
        imageView.setBorderRadius(40);
        imageView.setBackgroundColor(Color.GRAY);
        return imageView;
    }

    public static List<BannerEntity> getTestDatas(Context ctx, View.OnClickListener listener){
        List<BannerEntity> list = new ArrayList<>();
        ImageView imageView1 = new ImageView(ctx);
        ImageView imageView2 = new ImageView(ctx);
        ImageView imageView3 = new ImageView(ctx);
        ImageView imageView4 = new ImageView(ctx);
        ImageView imageView5 = new ImageView(ctx);

//        imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView4.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView5.setScaleType(ImageView.ScaleType.CENTER_CROP);

        GlideUtils.getInstance().LoadContextBitmap(ctx, "http://img03.tooopen.com/uploadfile/downs/images/20110714/sy_20110714135215645030.jpg", imageView1);
        GlideUtils.getInstance().LoadContextBitmap(ctx, "http://pic.58pic.com/58pic/15/68/59/71X58PICNjx_1024.jpg", imageView2);
        GlideUtils.getInstance().LoadContextBitmap(ctx, "http://gbres.dfcfw.com/Files/picture/20181027/45CFDF18A401B77C64CDDFF59F6E1A6A.jpg", imageView3);
        GlideUtils.getInstance().LoadContextBitmap(ctx, "http://pic.58pic.com/58pic/12/17/86/90958PICvpr.jpg", imageView4);
        GlideUtils.getInstance().LoadContextBitmap(ctx, "http://pic1.nipic.com/2008-08-14/2008814183939909_2.jpg", imageView5);

        list.add(new BannerEntity(ctx, imageView1, "111111", "http://www.baidu.com/", listener));
        list.add(new BannerEntity(ctx, imageView2, "222222", "http://www.baidu.com/", listener));
        list.add(new BannerEntity(ctx, imageView3, "333333", "http://www.baidu.com/", listener));
        list.add(new BannerEntity(ctx, imageView4, "444444", "http://www.baidu.com/", listener));
        list.add(new BannerEntity(ctx, imageView5, "555555", "http://www.baidu.com/", listener));
        return list;
    }
}
