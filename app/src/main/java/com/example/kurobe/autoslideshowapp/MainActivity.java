package com.example.kurobe.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;

    Handler mHandler = new Handler();

    Button mNextButton;
    Button mPrevButton;
    Button mStButton;
    ArrayList imageUris = new ArrayList();
    int nowId = 0;
    int maxId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }


        mNextButton = (Button) findViewById(R.id.next_btn);
        mPrevButton = (Button) findViewById(R.id.prev_btn);
        mStButton = (Button) findViewById(R.id.st_btn);


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                nowId++;
                if(nowId == maxId) {
                    nowId = 0;
                }
                imageView.setImageURI((Uri) imageUris.get(nowId));
            }
        });


        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                nowId--;
                if(nowId < 0) {
                    nowId = maxId - 1;
                }
                imageView.setImageURI((Uri) imageUris.get(nowId));
            }
        });

        mStButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTimer == null) {
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);

                        @Override
                        public void run() {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    nowId++;
                                    if (nowId == maxId) {
                                        nowId = 0;
                                    }
                                    imageView.setImageURI((Uri) imageUris.get(nowId));
                                }
                            });

                        }
                    }, 2000, 2000);
                    mStButton.setText("停止");
                    mNextButton.setEnabled(false);
                    mPrevButton.setEnabled(false);
                } else {
                    mTimer.cancel();
                    mTimer = null;
                    mStButton.setText("再生");
                    mNextButton.setEnabled(true);
                    mPrevButton.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default :
                break;
        }
    }

    private void getContentsInfo() {

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query (
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                imageUris.add(imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI((Uri) imageUris.get(0));
                maxId++;

            } while (cursor.moveToNext());
        }
        cursor.close();


    }

}
