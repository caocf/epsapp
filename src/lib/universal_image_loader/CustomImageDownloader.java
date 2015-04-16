package lib.universal_image_loader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.epeisong.data.exception.NetGetException;
import com.epeisong.logistics.proto.Eps.GetMediaBytesReq.Builder;
import com.epeisong.logistics.proto.Eps.GetMediaBytesResp;
import com.epeisong.ui.fragment.NetMediaGet;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ToastUtils;
import com.google.protobuf.TextFormat;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class CustomImageDownloader extends BaseImageDownloader {

    public CustomImageDownloader(Context context) {
        super(context);
    }

    public CustomImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }
    
    @Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        return getStreamFromNetwork(imageUri, extra);
    }

    @Override
    protected InputStream getStreamFromNetwork(final String imageUri, Object extra) throws IOException {
        if (imageUri == null) {
            ToastUtils.showToast("uri is null");
            throw new IOException();
        }
        if (imageUri.startsWith("http")) {
//            return super.getStreamFromNetwork(imageUri, extra);
        }
        NetMediaGet net = new NetMediaGet() {
            @Override
            protected boolean onSetRequest(Builder req) {
                req.setPictureUrl(imageUri);
                return true;
            }
        };
        InputStream in = null;
        try {
            GetMediaBytesResp.Builder resp = net.request();
            if (net.isSuccess(resp)) {
                byte[] data = resp.getMediaFile().toByteArray();
                in = new ByteArrayInputStream(data);
            }
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        if (in == null) {
            throw new IOException("NetMediaGet exception");
        }
        return in;
    }
}
