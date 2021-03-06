package com.example.mad_proj_fp1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.navigation.NavigationView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    public Uri currImageURI;
    public View v;
    private static final int pic_id = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        Button btn1 = findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });


    }


    // To handle when an image is selected from the browser, add the following to your Activity
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // currImageURI is the global variable I'm using to hold the content:// URI of the image
                currImageURI = data.getData();
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(currImageURI);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap myBitmap = BitmapFactory.decodeStream(is);

                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(15);
                myRectPaint.setColor(Color.GREEN);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                        .build();
                if (!faceDetector.isOperational()) {
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                }

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);

                for (int i = 0; i < faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }
                ImageView myImageView = (ImageView) findViewById(R.id.imageView2);
//                image.setImageBitmap(bitmap);
                myImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            }
            if (requestCode == pic_id) {
                Bitmap myBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(3);
                myRectPaint.setColor(Color.BLUE);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                        .build();
                if (!faceDetector.isOperational()) {
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                }
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);

                for (int i = 0; i < faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }
                ImageView myImageView = (ImageView) findViewById(R.id.imageView2);
//                image.setImageBitmap(bitmap);
                myImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_share:
                Toast.makeText(this, "Share Via!!", Toast.LENGTH_SHORT).show();
                Intent myIntent=new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody="Your Body Here";
                String shareSub="Your Subject here";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                startActivity(Intent.createChooser(myIntent,"Share Via"));
                return true;
            case R.id.nav_home:
                Toast.makeText(this, "Yo you are in home Now!!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_about:
                Toast.makeText(this, "Hey! This is About The App", Toast.LENGTH_SHORT).show();
                Intent send=new Intent(getBaseContext(),MainActivity2.class);
                startActivity(send);
                return true;
            case R.id.nav_about_us:
                Toast.makeText(this, "Directing to classroom", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://classroom.google.com/u/2/c/MTQ0NzgyMTA0NzQx"));
                startActivity(intent1);
                return true;
            case R.id.nav_rate:
                Toast.makeText(this, "Directing to playstore", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store"));
                startActivity(intent);
                return true;
            case R.id.nav_mentor:
                Toast.makeText(this, "Directing to our guide/mentor page", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://bmsce.ac.in/home/facultyProfile/146/SINDHU-K"));
                startActivity(intent2);

            default:
                return super.onOptionsItemSelected(item);
        }
    }




}




//    public String getRealPathFromURI(Uri contentUri) {
//
//        // can post image
//        String [] proj={MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery( contentUri,
//                proj, // Which columns to return
//                null,       // WHERE clause; which rows to return (all rows)
//                null,       // WHERE clause selection arguments (none)
//                null); // Order-by clause (ascending by name)
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//
//        return cursor.getString(column_index);
//    }
