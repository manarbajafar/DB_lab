package com.example.dblab;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteManger extends SQLiteOpenHelper {

    private static SQLiteManger sqLiteManger;
    private static final int Database_version =1;
    private static final String Database_Name ="NoteDB";
    private static final String Table_Name ="Notee";
    private static final String Counter ="Counter";

    private static final String ID_Field ="id";
    private static final String Title_Field ="title";
    private static final String Desc_Field ="Desc";
    private static final String Deleted_Field ="deleted";


    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateforamt = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public SQLiteManger(Context context){
        super(context, Database_Name, null , Database_version);
    }

    public static SQLiteManger instanceOfDatabase(Context context){
        if(sqLiteManger == null)
            sqLiteManger = new SQLiteManger(context);
        return  sqLiteManger;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql ;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(Table_Name)
                .append(" ( ")
                .append(Counter)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ID_Field)
                .append(" INT, ")
                .append(Title_Field)
                .append(" TEXT, ")
                .append(Desc_Field)
                .append(" TEXT, ")
                .append(Deleted_Field)
                .append(" TEXT )");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        switch ()
    }

    public void addNoteToDatabase (Note note){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_Field, note.getId());
        contentValues.put(Title_Field, note.getTitle());
        contentValues.put(Desc_Field, note.getDescription());
        contentValues.put(Deleted_Field, getStringFromDate(note.getDeleted()));
        System.out.println("ADDED!!!!!");
        sqLiteDatabase.insert(Table_Name, null, contentValues);
    }

    public void populateNoteListArray(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try(Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM "+ Table_Name, null ))
        {
            if(result.getCount() !=0 ){
                while (result.moveToNext()) {
                    int id = result.getInt(1);
                    String title =result.getString(2);
                    String desc =result.getString(3);
                    String stringDeleted = result.getString(4);
                    Date deleted = getDateFromString(stringDeleted);
                    System.out.println(id + title + desc + stringDeleted );
                    Note note = new Note (id,title,desc,deleted);
                    Note.noteArrayList.add(note);
                }
            }
        }

    }

    public void updateNoteDB(Note note ){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_Field, note.getId());
        contentValues.put(Title_Field, note.getTitle());
        contentValues.put(Desc_Field, note.getDescription());
        contentValues.put(Deleted_Field, getStringFromDate(note.getDeleted()));

        sqLiteDatabase.update(Table_Name, contentValues,ID_Field + " =? ", new String[]{String.valueOf(note.getId())});
    }

    private String getStringFromDate(Date date) {
        if (date ==null)
            return null;
        else
            return dateforamt.format(date);

    }

    private Date getDateFromString(String date)  {
        try {
            return dateforamt.parse(date);
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }
}
