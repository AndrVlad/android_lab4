package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        TableLayout tableLayout = findViewById(R.id.tableLayout);

        try {
            DBHelper myDatabaseHelper = new DBHelper(this);
            SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("songs", new String[] {"_id",
                    "singer","track_name", "TIME"},null, null, null, null, null);


            while (cursor.moveToNext()) {
                TableRow tableRow = new TableRow(this);
                String _id = cursor.getString(0);
                String singer = cursor.getString(1);
                String track_name = cursor.getString(2);
                String TIME = cursor.getString(3);

                TextView idTextView = new TextView(this);
                idTextView.setText(_id);
                idTextView.setPadding(8, 8, 8, 8);
                tableRow.addView(idTextView);

                TextView singerTV = new TextView(this);
                singerTV.setText(singer);
                singerTV.setPadding(8, 8, 8, 8);
                tableRow.addView(singerTV);

                TextView trackTV = new TextView(this);
                trackTV.setText(track_name);
                trackTV.setPadding(8, 8, 8, 8);
                tableRow.addView(trackTV);

                TextView TIMETextView = new TextView(this);
                TIMETextView.setText(TIME);
                TIMETextView.setPadding(8, 8, 8, 8);
                tableRow.addView(TIMETextView);

                tableLayout.addView(tableRow);
            }
            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}