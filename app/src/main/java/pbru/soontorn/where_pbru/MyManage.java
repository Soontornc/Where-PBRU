package pbru.soontorn.where_pbru;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by android on 27/5/2559.
 */
public class MyManage {
    private MyOpenHelper myOpenHelper;
    private SQLiteDatabase sqLiteOpenHelper;

    public MyManage(Context context) {

        myOpenHelper = new MyOpenHelper(context);
        sqLiteOpenHelper = myOpenHelper.getWritableDatabase();
    }   //Constructor

}   //Main Class
