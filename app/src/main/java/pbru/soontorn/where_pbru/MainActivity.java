package pbru.soontorn.where_pbru;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private MyManage myManage;
    private String urlJSON = "http://swiftcodingthai.com/pbru/get_room.php";
    private EditText editText;
    private String roomString;
    private String[] resultString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);


        myManage = new MyManage(this);

        //myManage.addRoom("build", "room", "lat", "lng", "icon"); //ทดสอบนำค่านี้ไปใส่

        deleteAllSQLite();

        SynRoom synRoom = new SynRoom();
        synRoom.execute();


    }   //Main Method

    public void clickSearch(View view) {

        roomString = editText.getText().toString().trim();

        if (roomString.equals("")) {
            Toast.makeText(this, "กรุณากรอกหมายเลขห้องก่อน", Toast.LENGTH_SHORT).show();

        } else {
            searchRoom();
        }


    }   //Click Serarch

    private void searchRoom() {
        try {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                    MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM roomTABLE WHERE Room = " + "'" + roomString + "'", null);
            cursor.moveToFirst();

            resultString = new String[cursor.getColumnCount()];

            for (int i=0;i<cursor.getColumnCount();i++) {

                resultString[i] = cursor.getString(i);

            }   //For

            myAlert();


        } catch (Exception e) {
            Toast.makeText(this, "ไม่มี" + roomString + "ในฐานข้อมูล", Toast.LENGTH_SHORT).show();
        }
    }   //Search Room

    private void myAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.build1);
        builder.setCancelable(false);
        builder.setTitle("ห้อง "+roomString);
        builder.setMessage("อยู่ที่อาคาร " + resultString[1]);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("Result", resultString);

                startActivity(intent);

                dialogInterface.dismiss();

            }
        });
        builder.show();
    }

    public void clickListBuild(View view) {


    }   //Click List Build

    public void clickAdmin(View view) {


    }

    public class SynRoom extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                return null;
            }

        }   //doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("whereV1", "JSON => " + s);

            try {

                JSONArray jsonArray = new JSONArray(s);

                for (int i=0;i<jsonArray.length();i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String strBuild = jsonObject.getString(MyManage.column_Build);
                    String strRoom = jsonObject.getString(MyManage.column_Room);
                    String strLat = jsonObject.getString(MyManage.column_Lat);
                    String strLng = jsonObject.getString(MyManage.column_Lng);
                    String strIcon = jsonObject.getString(MyManage.column_Icon);

                    myManage.addRoom(strBuild, strRoom, strLat, strLng, strIcon);

                }   //for


            } catch (Exception e) {
                e.printStackTrace();
            }

        }   //OnPost

    }   //SynRoom Class

    private void deleteAllSQLite() {
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                MODE_PRIVATE, null);
        sqLiteDatabase.delete(MyManage.room_TABLE, null, null);

    }
}   //Main Class
