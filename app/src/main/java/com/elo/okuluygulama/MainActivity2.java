package com.elo.okuluygulama;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listView;
    private EditText editTextAdSoyad;
    private EditText editTextNumber2;
    private EditText editTextEmail;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dbHelper = new DatabaseHelper(this); // Bağlamı 'this' olarak ayarlayın

        listView = findViewById(R.id.listView);
        editTextAdSoyad = findViewById(R.id.editTextText);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress2);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);
        // Verileri ListView'e yükle
        loadListViewData();
    }

    private void loadListViewData() {
        // Veritabanından kayıtları al
        ArrayList<String> dataList = dbHelper.getAllRecords();

        // ArrayAdapter kullanarak ListView'e verileri yükle
        adapter.clear();
        adapter.addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    // Geri butonu
    public void geri(View view) {
        Intent intent = new Intent(MainActivity2.this, MainActivity.class); // Hedef aktivite olarak MainActivity.class kullanın
        startActivity(intent);
    }

    // Buton 1'in açtığı link
    public void openLink1(View view) {
        openLink("https://www.erbakan.edu.tr/storage/files/department/seydisehirbilgisayar/Stajlar/STAJ%20Uygulama%20Kuralları.pdf");
    }

    // Buton 2'nin açtığı link
    public void openLink2(View view) {
        openLink("https://www.erbakan.edu.tr/storage/files/department/seydisehirbilgisayar/Stajlar/Staj_Formu_2024.pdf");
    }

    // Buton 3'ün açtığı link
    public void openLink3(View view) {
        openLink("https://www.erbakan.edu.tr/storage/images/department/seydisehirbilgisayar/Staj1dilekcesi_2024.pdf");
    }

    // Buton 4'ün açtığı link
    public void openLink4(View view) {
        openLink("https://www.erbakan.edu.tr/storage/images/department/seydisehirbilgisayar/Staj2dilekcesi_2024.pdf");
    }

    // Buton 5'in açtığı link
    public void openLink5(View view) {
        openLink("https://www.erbakan.edu.tr/storage/files/department/seydisehirahmetcengizmuhendislik/Matbu%20dilekceleri/Staj%20Ücretlerine%20İşsizlik%20Fonu%20Katkısı%20Öğrenci%20ve%20İşveren%20Bilgi%20Formu.pdf");
    }

    // Ortak link açma yöntemi
    public void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // Başvuru butonu
    public void basvurus(View view) {
        String adSoyad = editTextAdSoyad.getText().toString().trim();
        String okulNoStr = editTextNumber2.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();


        if (adSoyad.isEmpty() || okulNoStr.isEmpty() || email.isEmpty()) {
            Toast.makeText(MainActivity2.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        // Okul numarasının geçerli bir tamsayı olup olmadığını kontrol et
        int okulNo = 0;
        try {
            okulNo = Integer.parseInt(okulNoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity2.this, "Geçerli bir okul numarası girin", Toast.LENGTH_SHORT).show();
            return;
        }



        try {
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", adSoyad);
            values.put("number", Integer.parseInt(okulNoStr));

            long newRowId = database.insert("stajer", null, values);
            if (newRowId != -1) {
                Toast.makeText(MainActivity2.this, "Başvuru başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity2", "Yeni başvuru eklendi: " + adSoyad + " - " + okulNo);

                // Metin kutularını temizle
                editTextAdSoyad.setText("");
                editTextNumber2.setText("");
                editTextEmail.setText("");

                loadListViewData();
            } else {
                Toast.makeText(MainActivity2.this, "Başvuru kaydedilemedi.", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity2", "Başvuru eklenirken hata oluştu.");
            }

            // Verileri tekrar yükle


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity2", "Hata oluştu: " + e.getMessage());
        }
    }

    // DatabaseHelper sınıfı, SQLiteOpenHelper'dan türetilmiş olarak veritabanı işlemlerini yönetir
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "Stajer.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Veritabanı ilk oluşturulduğunda çağrılır, tabloyu oluşturur
            db.execSQL("CREATE TABLE IF NOT EXISTS stajer (name VARCHAR, number INT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Veritabanı sürümü değiştiğinde çağrılır, tabloyu günceller
            db.execSQL("DROP TABLE IF EXISTS stajer");
            onCreate(db);
        }

        // Veritabanından tüm kayıtları al
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
