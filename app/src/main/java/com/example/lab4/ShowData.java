package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;
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

                TextView idTextView = new TextView(this);
                idTextView.setText(cursor.getString(0));
                idTextView.setPadding(8, 8, 8, 8);
                idTextView.setMaxWidth(80);
                tableRow.addView(idTextView);

                TextView singerTV = new TextView(this);
                singerTV.setText(cursor.getString(1));
                singerTV.setPadding(8, 8, 8, 8);
                singerTV.setMaxWidth(400);
                singerTV.setEllipsize(TextUtils.TruncateAt.END);
                singerTV.setSingleLine(true);
                tableRow.addView(singerTV);

                TextView trackTV = new TextView(this);
                trackTV.setText(cursor.getString(2));
                trackTV.setPadding(8, 8, 8, 8);
                trackTV.setMaxWidth(400);
                trackTV.setEllipsize(TextUtils.TruncateAt.END);
                trackTV.setSingleLine(false); // Разрешаем перенос
                tableRow.addView(trackTV);

                TextView TIMETextView = new TextView(this);
                TIMETextView.setText(cursor.getString(3));
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