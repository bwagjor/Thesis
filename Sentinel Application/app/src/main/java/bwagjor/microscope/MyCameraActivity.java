package bwagjor.microscope;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class MyCameraActivity extends Activity
{
    private MicroTCPClient mMicroTcpClient;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String IPADDRESS = SP.getString("prefIP", "NA");


        Button send = (Button)findViewById(R.id.send_button);



        // connect to the server
        new connectTask(IPADDRESS).execute("");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               //Change this to button input String message = editText.getText().toString();



                //sends the message to the server
                if (mMicroTcpClient != null) {
                    //mMicroTcpClient.sendMessage(message);
                    System.out.println(IPADDRESS);
                }

            }
        });

    }

    public class connectTask extends AsyncTask<String,Bitmap,MicroTCPClient> {
        String IPAddress;
        public connectTask(String IP){
            IPAddress = IP;
        }
        @Override
        protected MicroTCPClient doInBackground(String... message) {

            //create a MicroTCPClient object
            mMicroTcpClient = new MicroTCPClient(new MicroTCPClient.OnMessageReceived() {
                @Override
                //messageReceived method is implemented
                public void messageReceived(Bitmap image) {
                    //call the onProgressUpdate
                    publishProgress(image);
                }
            },IPAddress);
            mMicroTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... image) {
            super.onProgressUpdate(image);

            ImageView display1 = (ImageView)findViewById(R.id.display1);
            display1.setImageBitmap(image[0]);
        }
    }
}