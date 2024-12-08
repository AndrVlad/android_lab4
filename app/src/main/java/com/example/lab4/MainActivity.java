package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase db;
    public static DBHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            myDBHelper = new DBHelper(this);
            db = myDBHelper.getWritableDatabase();

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        if(checkInternetConnection(MainActivity.this)) {
            new BackgroundTask().execute("Hello");
        };

    }

    public void onClickBtn1(View view) {
        Intent intent = new Intent(this, ShowData.class);
        startActivity(intent);
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Toast toast = Toast.makeText(this, "Подключение установлено", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else {
            Toast toast = Toast.makeText(this, "Нет подключения к Интернету", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
    }


}