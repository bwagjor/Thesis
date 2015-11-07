package bwagjor.microscope;

/**
 * Created by benja_000 on 1/05/2015.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AsyncDownloadImage extends AsyncTask<ImageView, Void, Bitmap> {

    private static final String TAG = "AsyncDownloadImage";
    public static final String SERVERIP = "10.89.190.124"; //your computer IP address
    public static final int SERVERPORT = 100;
    ImageView imageView = null;

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        this.imageView = imageViews[0];
        return DownloadImage();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null)
            imageView.setImageBitmap(result);
    }

    private Bitmap DownloadImage() {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            Socket socket = new Socket(SERVERIP, SERVERPORT);
            InputStream inputStream = socket.getInputStream();

            bitmap = BitmapFactory.decodeStream(in);
            if (in != null)
                in.close();
        } catch (IOException e1) {
            Log.e(TAG, "Error in downloading image");
            e1.printStackTrace();
        }
        return bitmap;
    }
}