package lib.grasp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import net.minidev.json.JSONAware;

import org.json.JSONArray;

import lib.grasp.widget.MessageBoxGrasp;


/**
 * Created by GaQu_Dev on 2018/10/31.
 */
public class PhoneUtil {
    /** 拨打电话（直接拨打电话） */
    public static void callPhone(final Context context, final String phoneNum) {
        if(!NumberUtil.isPhoneNum(phoneNum)){
            TOAST.showShort(context, "联系号码有误!");
            return;
        }

        MessageBoxGrasp.confirmMsg(context, "确认", "拨打公司客服电话[" + NumberUtil.getFormatPhone(phoneNum) + "]？", v -> {
            if(context instanceof Activity) if(!PermissionUtil.checkDangerousPermission((Activity) context, Manifest.permission.CALL_PHONE)) return;
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            context.startActivity(intent);
        }, null, true);
    }
}
