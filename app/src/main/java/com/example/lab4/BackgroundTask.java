package com.example.lab4;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class BackgroundTask extends AsyncTask<String, String, String> {
    String resultString = null;
    byte[] data = null;
    InputStream is = null;
    DBHelper myDatabaseHelper;
    SQLiteDatabase db;

    public MyAsyncTask() {
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = "https://media.itmo.ru/api_get_current_song.php";
        String login = "4707login";
        String password = "4707pass";
        String parametrs = "login=" + login + "&password=" + password;
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
            OutputStream os = connect .getOutputStream();
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
                String title = newSong.split("-")[1];

                myDatabaseHelper = MainActivity.DBHelper;
                db = MainActivity.myDB;
                String lastSong = myDatabaseHelper.last_record(db);
                if(!lastSong.equals(newSong)) {
                    myDatabaseHelper.add_into_table(db, singer, title);
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
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return resultString;
    }
}
