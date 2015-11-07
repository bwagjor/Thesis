package bwagjor.microscope;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MyActivity extends Activity
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
        //final ToggleButton tb = (ToggleButton) findViewById(R.id.liveStream);
        final Button send = (Button)findViewById(R.id.send_button);
        final Button save = (Button)findViewById(R.id.save);
        final Spinner lut = (Spinner)findViewById(R.id.lut_spinner);
        final SeekBar exposure = (SeekBar) findViewById(R.id.exposureBar);
        final TextView eText = (TextView) findViewById(R.id.expText);
        final EditText name = (EditText) findViewById(R.id.ImageName);
        lut.setOnItemSelectedListener(new MyOnItemSelectedListener());

        /*tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tb.isChecked()) {
                    send.setEnabled(false);
                    System.out.println("About to start liveStream");
                    new liveStream().execute("");
                } else {
                    send.setEnabled(true);

                }

            }
        });*/
        // connect to the server
        new connectTask(IPADDRESS).execute("");
        if (mMicroTcpClient != null) {
            mMicroTcpClient.sendMessage("Start");
            System.out.println(IPADDRESS);
        }

        //Set the listener for the refresh button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get image properties
                Spinner lut = (Spinner)findViewById(R.id.lut_spinner);
                String lut_result = String.valueOf(lut.getSelectedItem());
                SeekBar exposure = (SeekBar) findViewById(R.id.exposureBar);
                exposure.setMax(100);
                String exp =String.valueOf(exposure.getProgress() + 10);
                String message = "-"+exp+","+lut_result;


                //sends the message to the server
                if (mMicroTcpClient != null) {
                    mMicroTcpClient.sendMessage(message);
                    System.out.println(IPADDRESS);
                }
            }
        });

        //Set the on-click listener for the save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = (ImageView)findViewById(R.id.display1);
                EditText name = (EditText) findViewById(R.id.ImageName);
                //iv.buildDrawingCache();
               // Bitmap bm = iv.getDrawingCache();
                Bitmap bm = ((BitmapDrawable)iv.getDrawable()).getBitmap();
                //Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                //Bitmap bm = Bitmap.createBitmap(200, 200, conf); // this creates a MUTABLE bitmap
                System.out.println(Environment.getExternalStorageState());
                SaveImage(bm, name.getText().toString());

                //storeImage(iv.getDrawingCache()) ;
            }
        });
        exposure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                eText.setText(String.valueOf(progress + 10) + "ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void SaveImage(Bitmap finalBitmap,String name) {

        String root = Environment.getExternalStorageDirectory().toString();
        System.out.println(root);
        File myDir = new File(root + "/Sentinelsaved_images");
        myDir.mkdirs();
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String fname = name+"-"+ timeStamp +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            scanMedia(file.getAbsolutePath().toString());
            System.out.println("Should be saved as"+root+fname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class liveStream extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... fn) {
            System.out.println("Should have started");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mMicroTcpClient != null) {
                mMicroTcpClient.sendMessage("-30");
            }
            return null;
        }
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
    /**
     * Sends a broadcast to have the media scanner scan a file
     *
     * @param path
     *            the file to scan
     */
    private void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        sendBroadcast(scanFileIntent);
    }
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            System.out.println("Null media file");
            return;
        }
        try {
            System.out.println("Try saving image");
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            System.out.println("Should be saved");
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }
    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Sentinel/images/");

        //        + getApplicationContext().getPackageName()+ "/Files"
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        System.out.println("should have created media file");
        return mediaFile;
    }
}