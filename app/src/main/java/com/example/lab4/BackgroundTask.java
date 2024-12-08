package com.example.lab4;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BackgroundTask extends AsyncTask<String, String, String> {
    String resultString = null;
    byte[] data = null;
    InputStream is = null;
    DBHelper myDBHelper = MainActivity.myDBHelper;
    DateFormat df = new SimpleDateFormat("HH:mm:ss");
    static SQLiteDatabase db = MainActivity.db;

    public void MyAsyncTask() {
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
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
            //Сохранение соединения
            connect.setRequestProperty("Connection", "Keep-Alive");
            //Стандартное кодирование URL(Способ кодировки запроса)
            connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //Длина парамтера отправляемого на сервер
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

                resultString = newSong + singer + track;

                ContentValues studentValues = new ContentValues();
                String date = df.format(Calendar.getInstance().getTime());


                studentValues.put("singer", singer);
                studentValues.put("track_name", track);
                studentValues.put("TIME", date);
                db.insert("songs", null, studentValues);

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
        return resultString;
    }


}

