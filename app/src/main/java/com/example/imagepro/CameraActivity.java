package com.example.imagepro;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";

    private Mat mRgba;
    private CameraBridgeViewBase mOpenCvCameraView;
    // Call  for image view of flip button;
    private ImageView flip_camera;
    //integer that  represent camera
    //0 for back camera and 1 for front camera
    private int mCameraId = 0;

    //Call take_picture)button
    private ImageView take_picture_button;
    private int take_image = 0;
    //define that image view
    private ImageView image_gallery_icon;

    // localisation variable
    LocationManager locationManager;
    String latitude, longitude;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface
                        .SUCCESS: {
                    Log.i(TAG, "OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default: {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
        }


        ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        flip_camera = findViewById(R.id.flip_camera);
        // when flit camera button is cliked
        flip_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this function will change camera
                swapCamera();
            }
        });

        take_picture_button = findViewById(R.id.take_picture_button);
        take_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (take_image == 0) {


                    take_image = 1;
                } else {
                    take_image = 0;
                }
            }
        });
        image_gallery_icon = findViewById(R.id.image_gallery_icon);
        image_gallery_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if this button is clicked, navigate to new activity(Gallery)
                //First we will create new activity and use startActivity to navigate
                startActivity(new Intent(CameraActivity.this, GalleryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetWork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPossive = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (LocationGps != null) {
                double lat = LocationGps.getLatitude();
                double longi = LocationGps.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.d("Valeur", "Ici leur Valeur Lat est : " + lat + " et Long est :" + longi);
            }
            if (LocationNetWork != null) {
                double lat = LocationNetWork.getLatitude();
                double longi = LocationNetWork.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.d("Valeur", "Ici leur Valeur Lat est : " + lat + " et Long est :" + longi);
            }
            if (LocationPossive != null) {
                double lat = LocationPossive.getLatitude();
                double longi = LocationPossive.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.d("Valeur", "Ici leur Valeur Lat est: " + lat + " et Long est :" + longi);
            } else {
                Log.d("Valeur", "RAS " + " Pas de localisation");
            }
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void swapCamera() {
        //first we will change mCameraId
        // if 0 change it to 1
        // if 1 change it to 0
        mCameraId = mCameraId ^ 1;
        mOpenCvCameraView.disableView();
        mOpenCvCameraView.setCameraIndex(mCameraId);
        mOpenCvCameraView.enableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            //if load success
            Log.d(TAG, "Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            //if not loaded
            Log.d(TAG, "Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC3);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();


        if (mCameraId == 1) {
            Core.flip(mRgba, mRgba, 1);
        }
        take_image = take_picture_function_rgb(take_image, mRgba);

        return mRgba;

    }

    private Bitmap mat2Bitmap(Mat src, int code) {
        Mat rgbaMat = new Mat(src.width(), src.height(), CvType.CV_8UC4);
        Imgproc.cvtColor(src, rgbaMat, code, 3);
        Bitmap bmp = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgbaMat, bmp);
        return bmp;
    }

    //Mat to bitmap
    private static Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }


    //Changer le bit d'une couleur
    private int changer_bit(char msg, int val) {
        String s = Integer.toBinaryString(val);
        int comp = s.length();
        for (int i = 0; i < 8 - comp; i++) {
            s = "0" + s;
        }
        s = s.substring(0, 7) + msg ;
        int result = Integer.parseInt(s, 2);
        return result;

    }

    //Avqnt dernier bit
    private char av_dernier_bit(int nombre) {
        String s = Integer.toBinaryString((int) nombre);
        int comp = s.length();
        for (int i = 0; i < 8 - comp; i++) {
            s = "0" + s;
        }
        return s.charAt(6);
    }

    //Dernier bit
    private char dernier_bit(int nombre) {
        String s = Integer.toBinaryString(nombre);
        int comp = s.length();
        //for (int i = 0; i < 8 - comp; i++) {
        //    s = "0" + s;
        //}
        return s.charAt(s.length()-1);
    }




    //Retourne la bit du pixel
    private int rgb_val(double[] pix, char cle) {
        if (av_dernier_bit((int) pix[0]) == av_dernier_bit((int) pix[1]) && av_dernier_bit((int) pix[0]) == av_dernier_bit((int) pix[2]) && av_dernier_bit((int) pix[0]) == cle) {
            return 1;
        } else {
            return 0;
        }

    }

    //Cette fonction permet de modifier le bit d'un pixel
    private int modifier_pixel(char cle, char msg, double[] p, int i) {
        if (rgb_val(p, cle) == 1) {
            return 1;
        } else {
            if (av_dernier_bit((int) p[i]) != cle) {
                return 0;
            } else
                return -1;
        }
    }

    //Convertir en binaire une chaine
    private String message2binaire(String s) {
        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }

        }
        return binary.toString();
    }

    //Chaine binaire en chaine de carateres

    private String binaire2string(String binaire){
        String chaine="";
        int count=0;
        int deci;
        String mess="";

        while (count<binaire.length()){
            chaine+=binaire.substring(count,count+8);
            count+=8;
            //chaine=Integer.parseInt(chaine);
            deci= Integer.parseInt(chaine,2);
            mess+=(char)deci;
            chaine="";
        }
        return mess;
    }




    // Extraction des donnee
    private String extraction_message(String cle, Mat img,int longueurMess)

    {
        int numbers;
        //cle = cle[0:cle.find(str(taille), 3) - 1]
        int taille_messa = longueurMess * 8;
        cle = message2binaire(cle);
        int m = 0;
        int k = 0;
        double[] pixel;
        int ligne=img.rows(), colonne=img.cols();
        String  mess = "";
        for(int i=0; i<ligne;i++){
             for(int j=0; j<colonne;j++){
                 if (k == cle.length())
                     k = 0;
                 if (m == taille_messa)
                     break;
                 pixel = img.get(i,j);
                 for(int l=2;l>=0;l--){
                     if (k == cle.length())
                         k = 0;
                     if (m == taille_messa)
                         break;
                     if (rgb_val(pixel, cle.charAt(k)) == 1){
                             mess=mess+dernier_bit((int) pixel[l]);
                             m += 1;
                             k += 1;
                     }else if (av_dernier_bit((int) pixel[l]) != cle.charAt(k)) {
                         mess=mess+dernier_bit((int) pixel[l]);
                         m += 1;
                         k += 1;
                     }
                 }
                 if (k == cle.length())
                     k = 0;
                 if (m == taille_messa)
                     break;
             }

             }
        return mess;
    }





    private int take_picture_function_rgb(int take_image, Mat mRgba) {


        if (take_image == 1) {

            //first add permission for writing in local storage

            Mat save_mat = new Mat();
            //Rotate image by 90 degree
            Core.flip(mRgba.t(), save_mat, 1);

            //convert image from RGBA to BGRA

            //Imgproc.cvtColor(save_mat, save_mat, Imgproc.COLOR_RGBA2BGRA);
            Imgproc.cvtColor(save_mat, save_mat, Imgproc.COLOR_BGRA2RGB);
            //creat a new folder PhotoPro, and we will save all image into that folder
            File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/ImagePro");
            //check if folder exist , if not creat a new folder
            Log.d("take", "success: " + folder);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            // Log.d("take", "success: " + file);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateAndTime = sdf.format(new Date());
            //String fileName = Environment.getExternalStorageDirectory().getPath() + "/ImagePro/" + currentDateAndTime + ".jpg";

            //Imgcodecs.imwrite(fileName, save_mat);

            //conversion RGBA to RGB
            //Imgproc.cvtColor(save_mat, save_mat, Imgproc.COLOR_RGBA2RGB);
            //Imgcodecs.imwrite(fileName,save_mat);


            // get location
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
            } else {
                getLocation();
            }

            //modifier pixel
            String fileName = Environment.getExternalStorageDirectory().getPath() + "/ImagePro/" + currentDateAndTime + ".png";

            Imgcodecs.imwrite(fileName, save_mat);



            Mat mImg = save_mat;


           // Mat tmp = new Mat (bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);


            // codage image
            String text="Je travaille la nuit cette annee 2022!! ";
            String cle="2022";
            String laCle=cle;
            String message = message2binaire(text);
            int m = 0;
            int k = 0;
            double[] pixel;
            cle = message2binaire(cle);
            int colonne = mImg.cols();
            int ligne = mImg.rows();
            Log.d("dim", "colonne: " + colonne +" , ligne: " + ligne);
            Log.d("dim", "Cle bin : " + cle +" , message bin: "+ message);


           /* for(int i=0 ; i<5;i++)
                for (int j=0;j<3;j++){
                    pixel = save_mat.get(i, j);
                    Log.d("valo1", "length: "+ pixel.length+ "pixel " + i+" "+ j+ " pixel: [" + pixel[2]+", "+pixel[1]+", "+pixel[0]+"]");
                }*/
            for(int i=0 ; i<=ligne-1;i++){
                for (int j=0;j<=colonne-1;j++){
                    pixel = mImg.get(i, j);
                    if(k== cle.length()){
                        k=0;
                    }
                    if(m==message.length()){
                        break;
                    }
                //Log.d("valo", "length: "+ pixel.length+ "pixel " + i+" "+ j+ " pixel: [" + pixel[0]+", "+pixel[1]+", "+pixel[2]+"]");
                    for(int l=2;l>=0;l--)
                    {
                        if(m==message.length()){
                            break;
                        }
                        if(k==cle.length()){
                            k=0;
                        }
                        if (rgb_val(pixel, cle.charAt(k)) == 1) {
                            pixel[l] = changer_bit(message.charAt(m),(int) pixel[l]);
                            m += 1;
                            k += 1;
                            mImg.put(i, j, pixel);
                        } else
                            if (av_dernier_bit((int) pixel[l]) != cle.charAt(k)) {
                                pixel[l] = changer_bit(message.charAt(m), (int)pixel[l]);
                                m += 1;
                                k += 1;
                                mImg.put((int)i, (int)j, pixel);
                            }
                         Log.d("valo1", "length: "+ pixel.length+ " pixel " + i+" "+ j+ " pixel: [" + pixel[0]+", "+pixel[1]+", "+pixel[2]+"]");
                        // Log.d("valo2", "length: "+  pixel.length+ " pixel " + i+" "+ j+ " pixel: [" + save_mat.get(i,j)[0]+", "+save_mat.get(i,j)[1]+", "+save_mat.get(i,j)[2]+"]");

                    }
                    if (m==message.length()){
                        break;
                    }
                    if (k==cle.length()){
                        k = 0;
                    }
                    Log.d("valo", "length: "+ pixel.length+ " pixel " + i+" "+ j+ " pixel: [" + pixel[2]+", "+pixel[1]+", "+pixel[0]+"]");

                }
            }

            String messa=extraction_message(laCle,mImg,text.length());
            Log.d("Messagerie", "Donnee: "+ binaire2string(messa) );

            //Bitmap image = mat2Bitmap(save_mat, Imgproc.COLOR_RGBA2RGB);
            // Bitmap image = convertMatToBitMap(save_mat);
            //MediaStore.Images.Media.insertImage(getContentResolver(), image, currentDateAndTime + text.length(), "");

             fileName = Environment.getExternalStorageDirectory().getPath() + "/ImagePro/" + currentDateAndTime+ "yes" + ".png";

            Imgcodecs.imwrite(fileName, mImg);

            Mat im2 = Imgcodecs.imread(fileName);
            String mess=extraction_message(laCle,im2,text.length());
            Log.d("Messagerie", "Donnee1: "+ binaire2string(mess) );

      /*      Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            int pix;
            for(int i=0 ; i<ligne;i++)
                for (int j=0;j<colonne;j++)
                {
                    pix=bitmap.getPixel(i,j);
                    Log.d("Yoo", "Donnee: " + Color.red(pix));
                }*/
            take_image = 0;

        };

        return take_image;

    }
}
