package com.quickdoodle.challengelearn.quickdoodle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.google.android.gms.ads.InterstitialAd;


public class draw extends AppCompatActivity {
    private AdView mAdView;
    private CanvasView mcanvasView;
    TextView mchrono, clearstart, mdraw;
    boolean done = false;
    ImageView mImageDy, mhide;
    Button mstart, mrenewdraw;
    String[] name_list = {"Anything", "a Cookie","a Fridge","a Couch", "a Crab",
            "a Cow", "a Crayon", "a Boat", "a Crown", "a jewel", "a Cup", "a Dog",
            "a Delphine", "a Door", "a Dragon", "a Donate", "a Flower", "a Face",
            "Glasses", "a Fish", "an Elephant"};


    private InterstitialAd mInterstitialAd;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        Random randint = new Random();
        int val = randint.nextInt(20);




        mcanvasView = (CanvasView) findViewById(R.id.drawP);
        mchrono = (TextView) findViewById(R.id.chrono);
        mdraw = (TextView) findViewById(R.id.drawing);
        mImageDy = (ImageView) findViewById(R.id.imageDy);
        //mImageDy.setImageResource(R.drawable.img3);
        mstart = (Button) findViewById(R.id.start);
        clearstart = (TextView) findViewById(R.id.clickstart);
        mhide = (ImageView) findViewById(R.id.hide);
        mrenewdraw = (Button) findViewById(R.id.renewdraw);

        mdraw.setText(" Draw "+ name_list[val]);
        imagenumber(val);


        final CountDownTimer countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                mchrono.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mchrono.setText("done!");
                final Bitmap b = Screenshot.takescreenshotOfRootView(mcanvasView);
                //mImageDy.setImageBitmap(b);
                //startActivity(mresult);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(draw.this );
                View mView = getLayoutInflater().inflate(R.layout.dialoge_result, null);
                ImageView yourArt = (ImageView) mView.findViewById(R.id.imageart);
                Button renewArt = (Button) mView.findViewById(R.id.renewit);
                Button saveArt = (Button) mView.findViewById(R.id.saveit);
                Button shareArt = (Button) mView.findViewById(R.id.shareit);
                yourArt.setImageBitmap(b);

                //share
                shareArt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareimage(b);
                    }
                });

                //save image
                saveArt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startsave(b);
                    }
                });
                mBuilder.setView(mView);
                final AlertDialog mdialog = mBuilder.create();
                mdialog.show();

                //restart
                renewArt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent retour =new Intent(draw.this, draw.class);
                        startActivity(retour);
                        finish();
                        //mdialog.dismiss();
                        //mcanvasView.clearCanvas();
                        //mchrono.setText("00:20 sec");
                        //clearstart.setText("Click start icon to start");
                    }
                });
            }
        };



        mrenewdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent change =new Intent(draw.this, draw.class);
                startActivity(change);
                finish();
            }
        });

        mstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.start();
                mhide.setVisibility(View.GONE);
                mcanvasView.clearCanvas();
                clearstart.setText("");
            }
        });


        mhide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                countDownTimer.start();
                mhide.setVisibility(View.GONE);
                mcanvasView.clearCanvas();
                clearstart.setText("");
                return false;
            }
        });

    }

    public void startsave(Bitmap b){

        Toast.makeText(this, "Saving", Toast.LENGTH_SHORT).show();

        try{
            //String path = Environment.getExternalStorageDirectory().toString();
            //OutputStream fOut = null;
            File file = savebitmap(b);
            MediaStore.Images.Media.insertImage(getContentResolver()
                    ,file.getAbsolutePath(),file.getName(),file.getName());

            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
            refresh(file);


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File savebitmap(Bitmap bmp) throws IOException {
        String filename;
        Date date = new Date(0);
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        filename =  sdf.format(date);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                +"/DCIM/Camera/"+"IMG_"+filename+".jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }

    public void refresh(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }


    public void shareimage(Bitmap bitmap){
        //sharing pics
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory()+"/DCIM/"+"imageDemo.jpg");
        String path = Environment.getExternalStorageDirectory()+"/DCIM/"+"imageDemo.jpg";
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:" + path));
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }




    public void imagenumber(int num){
        switch (num) {
            case 1:
                mImageDy.setImageResource(R.drawable.img1);
                break;
            case 2:  mImageDy.setImageResource(R.drawable.img2);
                break;
            case 3:  mImageDy.setImageResource(R.drawable.img3);
                break;
            case 4:  mImageDy.setImageResource(R.drawable.img4);;
                break;
            case 5:  mImageDy.setImageResource(R.drawable.img5);
                break;
            case 6:  mImageDy.setImageResource(R.drawable.img6);
                break;
            case 7:  mImageDy.setImageResource(R.drawable.img7);
                break;
            case 8:  mImageDy.setImageResource(R.drawable.img8);
                break;
            case 9:  mImageDy.setImageResource(R.drawable.img9);
                break;
            case 10: mImageDy.setImageResource(R.drawable.img10);
                break;
            case 11: mImageDy.setImageResource(R.drawable.img11);
                break;
            case 12: mImageDy.setImageResource(R.drawable.img12);
                break;
            case 13: mImageDy.setImageResource(R.drawable.img13);
                break;
            case 14: mImageDy.setImageResource(R.drawable.img14);
                break;
            case 15: mImageDy.setImageResource(R.drawable.img15);
                break;
            case 16: mImageDy.setImageResource(R.drawable.img16);
                break;
            case 17: mImageDy.setImageResource(R.drawable.img17);
                break;
            case 18: mImageDy.setImageResource(R.drawable.img18);
                break;
            case 19: mImageDy.setImageResource(R.drawable.img19);
                break;
            case 20: mImageDy.setImageResource(R.drawable.img20);
                break;
            default: mImageDy.setImageResource(R.drawable.img0);
                break;
        }
        System.out.println(num);
    }



}
