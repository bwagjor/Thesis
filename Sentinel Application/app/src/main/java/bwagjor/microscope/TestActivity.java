package bwagjor.microscope;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class TestActivity extends Activity
{
    private TestTCP mTcpClient;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mic_view);
        imageView = (ImageView) findViewById(R.id.imageView);



        // connect to the server
       // new connectTask().execute("");



    }
/*
    public class connectTask extends AsyncTask<String,byte[],MicroTCPClient> {

        @Override
        protected TestTCP doInBackground(String... message) {

            //we create a MicroTCPClient object and
            mTcpClient = new TestTCP(new MicroTCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(byte[]... image) {
            super.onProgressUpdate(image);

            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,image.length);

            imageView.setImageBitmap(bitmap);
        }
    } */
}