package ut.ece1778.bean;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * SQLITE helper class to create the default table t_goals for
 * storing goals parsed from MySQL database.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 **/
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campusnaut";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE t_goals (goal_id INTEGER PRIMARY KEY, title TEXT, description " +
        		"TEXT, latitude TEXT, longitude TEXT, category TEXT, state INTEGER, ondisk TEXT);");
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.w("t_goals", "Upgrading database, which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS t_goals");
        
        onCreate(db);
    }
    
   
}
