package lib.universal_image_loader;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.epeisong.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageLoaderUtils {

    private static DisplayImageOptions listOptions;
    private static DisplayImageOptions listOptionsForUserLogo;
    private static DisplayImageOptions detailOptions;

    public static DisplayImageOptions getListOptions() {
        int nDefId = R.drawable.chatting_more_photo;
        if (listOptions == null) {
            listOptions = new DisplayImageOptions.Builder().showImageOnLoading(nDefId).showImageForEmptyUri(nDefId)
                    .showImageOnFail(nDefId).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                    .cacheOnDisk(true).cacheInMemory(true).bitmapConfig(Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(10)).build();
        }
        return listOptions;
    }

    public static DisplayImageOptions getListOptionsForUserLogo() {
        if (listOptionsForUserLogo == null) {
            listOptionsForUserLogo = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.user_logo_default).showImageOnFail(R.drawable.user_logo_default)
                    .cacheOnDisk(true).cacheInMemory(true).bitmapConfig(Config.RGB_565).build();
        }
        return listOptionsForUserLogo;
    }

    public static DisplayImageOptions getDetailOptions() {
        if (detailOptions == null) {
            detailOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_launcher)
                    .showImageOnFail(R.drawable.ic_launcher).resetViewBeforeLoading(true).cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();
        }
        return detailOptions;
    }
}
