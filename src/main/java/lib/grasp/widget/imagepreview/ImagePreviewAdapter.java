package lib.grasp.widget.imagepreview;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lib.grasp.util.glide.GlideUtils;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by JS_grasp on 2019/1/23.
 */
public class ImagePreviewAdapter extends PagerAdapter {
    private Context mCtx;
    private List<PhotoView> datas = new ArrayList<>();
    private PhotoViewAttacher.OnViewTapListener mTapListener;

    public ImagePreviewAdapter(Context ctx, List<String> list, PhotoViewAttacher.OnViewTapListener listener) {
        this.mCtx           = ctx;
        this.mTapListener   = listener;
        for(String url : list){
            PhotoView photoView = new PhotoView(mCtx);
            GlideUtils.getInstance().LoadContextBitmap(mCtx, url, photoView);
            datas.add(photoView);
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = datas.get(position);
        if(mTapListener != null) photoView.setOnViewTapListener(mTapListener);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(datas.get(position));
    }
}
