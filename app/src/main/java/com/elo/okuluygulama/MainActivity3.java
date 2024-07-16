package com.elo.okuluygulama;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private EditText editTextText2;
    private EditText editTextTextNumber;
    private EditText editTextEmailAddress;
    private DatabaseHelper dbHelper;
    private ListView listView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        dbHelper = new DatabaseHelper(this);

        editTextText2 = findViewById(R.id.editTextText2);
        editTextTextNumber = findViewById(R.id.editTextNumber);
        editTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        listView = findViewById(R.id.listView1);

        loadListViewData();
    }

    private void loadListViewData() {

        ArrayList<String> dataList = dbHelper.getAllRecords();

        // ArrayAdapter kullanarak ListView'e verileri yükle
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, dataList);
        listView.setAdapter(adapter);

    }

    public void openLink6(View view) {
        openLink("https://www.erbakan.edu.tr/storage/files/department/seydisehirahmetcengizmuhendislik/Intorn%20Muhendislik/İntörn%20Başvuru%20Dilekçesi.pdf");
    }

    public void openLink7(View view) {
        openLink("https://www.erbakan.edu.tr/storage/images/department/seydisehirahmetcengizmuhendislik/Duyuru%20Fotografları/fakultemizde%20is%20eri%20egitimi%20basliyor/SACMF%20İşyeri%20Eğitimi%20Aday%20Mühendis%20Talep%20Formu.pdf");
    }

    public void openLink8(View view) {
        openLink("https://www.erbakan.edu.tr/storage/images/department/seydisehirahmetcengizmuhendislik/Duyuru%20Fotografları/fakultemizde%20is%20eri%20egitimi%20basliyor/İŞYERİ%20EĞİTİMİ%20PROTOKOLÜ%20%20%20SACMF%20son.pdf");
    }

    public void openLink9(View view) {
        openLink("https://www.erbakan.edu.tr/storage/images/department/seydisehirahmetcengizmuhendislik/Duyuru%20Fotografları/fakultemizde%20is%20eri%20egitimi%20basliyor/işyeri%20eğitimi%20uygulama%20yönergesi%28Fen%20ve%20Müh%20Fak%29.pdf");
    }

    public void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void don(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void basvurui(View view) {
        String adSoyad = editTextText2.getText().toString().trim();
        String projekoduStr = editTextTextNumber.getText().toString().trim();
        String email = editTextEmailAddress.getText().toString().trim();

        if (adSoyad.isEmpty() || projekoduStr.isEmpty() || email.isEmpty()) {
            Toast.makeText(MainActivity3.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        int projekodu = 0;
        try {
            projekodu = Integer.parseInt(projekoduStr);
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity3.this, "Geçerli bir proje kodu girin", Toast.LENGTH_SHORT).show();
            return;
        }



        try {
            SQLiteDatabase database1 = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", adSoyad);
            values.put("number", Integer.parseInt(String.valueOf(projekodu)));

            long newRowId = database1.insert("stajer", null, values);
            if (newRowId != -1) {
                Toast.makeText(MainActivity3.this, "Başvuru başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity3", "Yeni başvuru eklendi: " + adSoyad + " - " + projekodu);
                loadListViewData();

                // Metin kutularını temizle
                editTextText2.setText("");
                editTextTextNumber.setText("");
                editTextEmailAddress.setText("");
            } else {
                Toast.makeText(MainActivity3.this, "Başvuru kaydedilemedi.", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity3", "Başvuru eklenirken hata oluştu.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity3", "Hata oluştu: " + e.getMessage());
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "Stajer.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS stajer (name VARCHAR, number INT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS stajer");
            onCreate(db);
        }

        public ArrayList<String> getAllRecords() {
            ArrayList<String> dataList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM stajer", null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") int number = cursor.getInt(cursor.getColumnIndex("number"));
                    dataList.add(name + " - " + number);
                } while (cursor.moveToNext());
            }

            cursor.close();
            return dataList;
        }
    }
}
