package lib.grasp.widget.banner.snap;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import lib.grasp.R;
import lib.grasp.widget.imagepreview.ImagePreviewPop;

/**
 * Created by GaQu_Dev on 2018/9/26.
 */
public class SnapBannerEntity {
    /** 点击跳转url */
    public String id;
    /** 展示文字 */
    public String title;
    /** 图片url */
    public String imgUrl;
    /** 指示器 */
    public IconTextView itv;

    public SnapBannerEntity(Context ctx, String id, String title, String imgUrl) {
        this.id     = id;
        this.title  = title;
        this.imgUrl = imgUrl;
        this.itv    = getInitedItv(ctx);
    }

    public static List<SnapBannerEntity> getTestDatas(Context ctx, View.OnClickListener listener){
        List<SnapBannerEntity> list = new ArrayList<>();
        list.add(new SnapBannerEntity(ctx, "1", "title_111111", "http://upload.mnw.cn/2019/0121/1548060286397.jpg"                                     ));
        list.add(new SnapBannerEntity(ctx, "2", "title_222222", "http://pic1.nipic.com/2008-12-30/200812308231244_2.jpg"                               ));
        list.add(new SnapBannerEntity(ctx, "3", "title_333333", "http://gbres.dfcfw.com/Files/picture/20181027/45CFDF18A401B77C64CDDFF59F6E1A6A.jpg"   ));
        list.add(new SnapBannerEntity(ctx, "4", "title_444444", "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg"                     ));
        list.add(new SnapBannerEntity(ctx, "5", "title_555555", "http://pic1.nipic.com/2008-08-14/2008814183939909_2.jpg"                              ));
        return list;
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
}
