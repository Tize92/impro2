package com.example.imagepro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
//For sliding effect we will use ViewPager
    ViewPager mViewPager;
    // now we will create new Array
    ArrayList<String> filePath=new ArrayList<>();
    ViewPageAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        //Now we will create File for path Image
      // File folder=new File(Environment.getExternalStorageDirectory().getPath()+"/ImagePro");
        //New we will create new function which will add all file to array
       // createFileArray(folder);
        filePath=listOfImages(GalleryActivity.this);

        //now we will create new class extents PageAdapter
        mViewPager=(ViewPager) findViewById(R.id.viewPageMain);
        viewPageAdapter=new ViewPageAdapter(GalleryActivity.this,filePath);
        mViewPager.setAdapter(viewPageAdapter);


    }

    private void createFileArray(File folder) {
        //Convert File to listFile
        File listFile[]=folder.listFiles();

        //if it is not empty. loop through each image
        if (listFile !=null)
            for (File file : listFile) {
                filePath.add(file.getAbsolutePath());

            }
    }

    private ArrayList<String> listOfImages(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages= new ArrayList<>();
        String absolutePathOfImages;
        uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d("gallery",uri.toString());
        String[] projection ={MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy= MediaStore.Video.Media.DATE_TAKEN;
        cursor= context.getContentResolver().query(uri,
                projection,
                null,
                null,
                orderBy+" DESC");
        column_index_data= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while ((cursor.moveToNext())){
            absolutePathOfImages=cursor.getString(column_index_data);
            Log.d("gallery",absolutePathOfImages);
            listOfAllImages.add(absolutePathOfImages);
        }
        return  listOfAllImages;
    }


}