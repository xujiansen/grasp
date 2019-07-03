package lib.grasp.util;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;

import lib.grasp.widget.MessageBoxGrasp;


/**
 * Created by GaQu_Dev on 2018/10/31.
 */
public class PhoneUtil {
    /** 拨打电话（直接拨打电话） */
    public static void callPhone(AppCompatActivity activity, final String phoneNum) {
        if(!NumberUtil.isPhoneNum(phoneNum)){
            TOAST.showShort(activity, "联系号码有误!");
            return;
        }

        MessageBoxGrasp.confirmMsg(activity, "确认", "拨打公司客服电话[" + NumberUtil.getFormatPhone(phoneNum) + "]？", v -> {
            if (!PermissionRxUtil.checkDangerousPermission(activity, android.Manifest.permission.CALL_PHONE)) return;
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            activity.startActivity(intent);
        }, null, true);
    }
}
