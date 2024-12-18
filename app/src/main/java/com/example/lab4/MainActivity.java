package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

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
        runAsyncTask();

    }

    public void onClickBtn1(View view) {
        Intent intent = new Intent(this, ShowData.class);
        startActivity(intent);
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;
        } else {
            Toast toast = Toast.makeText(this, "Нет подключения к Интернету", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
    }

    private void runAsyncTask() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(checkInternetConnection(MainActivity.this)) {
                    new BackgroundTask().execute("Hello");
                    //runAsyncTask();
                };

                handler.postDelayed(this, 20000);
            }
        });
    }

    public class BackgroundTask extends AsyncTask<String, Void, Boolean> {
        String resultString = null;
        byte[] data = null;
        InputStream is = null;
        //DBHelper myDBHelper = MainActivity.myDBHelper;
        DBHelper myDBHelper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        DateFormat df = new SimpleDateFormat("HH:mm:ss");


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean results = false;
            String urlString = "https://media.itmo.ru/api_get_current_song.php";
            String login = "4707login";
            String password = "4707pass";
            String parametrs = null;
            try {
                parametrs = "login=" + URLEncoder.encode(login, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String method = "POST";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setReadTimeout(10000);
                connect.setConnectTimeout(15000);
                connect.setRequestMethod(method);

                connect.setRequestProperty("Connection", "Keep-Alive");

                connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                connect.setRequestProperty("Content-Length", "" + Integer.toString(parametrs.getBytes().length));
                connect.setDoOutput(true);
                connect.setDoInput(true);

                data = parametrs.getBytes("UTF-8");
                OutputStream os = connect.getOutputStream();
                os.write(data);
                os.flush();
                os.close();
                data = null;
                connect.connect();
                int responseCode = connect.getResponseCode();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (responseCode == 200) {
                    is = connect.getInputStream();
                    byte[] buffer = new byte[8192];

                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    data = baos.toByteArray();
                    resultString = new String(data, "UTF-8");
                    JSONObject responseJSON = new JSONObject(resultString);
                    String newSong = responseJSON.getString("info");
                    String singer = newSong.split("-")[0];
                    String track = newSong.split("-")[1];

                    Cursor cursor = db.query("songs", new String[] {"MAX(_id) as max_id"},null, null, null, null, null);
                    Integer id_key;

                    if(cursor.moveToFirst()) {
                        id_key = cursor.getInt(0);
                    } else {
                        id_key = 0;
                    }

                        if (id_key == 0) {
                            ContentValues songValues = new ContentValues();
                            String date = df.format(Calendar.getInstance().getTime());
                            songValues.put("singer", singer);
                            songValues.put("track_name", track);
                            songValues.put("TIME", date);
                            results = true;
                            if ((db.insert("songs", null, songValues)) == -1)
                                results = false;

                        } else {

                            Cursor cursor_1 = db.query ("songs",
                                    new String[] {"_id","singer", "track_name"},
                                    "_id = ?",
                                    new String[] {Integer.toString(id_key)},
                                    null, null,null);
                            if (cursor_1.moveToFirst()) {
                                String singer_db = cursor_1.getString(1);
                                String track_name_db = cursor_1.getString(2);

                                if (!Objects.equals(singer_db, singer) && !Objects.equals(track_name_db, track)) {
                                    ContentValues songValues = new ContentValues();
                                    String date = df.format(Calendar.getInstance().getTime());
                                    songValues.put("singer", singer);
                                    songValues.put("track_name", track);
                                    songValues.put("TIME", date);
                                    results = true;
                                    if((db.insert("songs", null, songValues)) == -1)
                                        results = false;
                                }
                            }
                        }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        protected void onPostExecute(Boolean success) {
            if(success) {
                Toast toast = Toast.makeText(MainActivity.this,
                       "Запись добавлена", Toast.LENGTH_SHORT);
                toast.show();
            }
        }


    }


}