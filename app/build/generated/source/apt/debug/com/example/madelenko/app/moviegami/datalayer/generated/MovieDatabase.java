package com.example.madelenko.app.moviegami.datalayer.generated;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.madelenko.app.moviegami.datalayer.MovieTables;
import java.lang.Override;
import java.lang.String;

public class MovieDatabase extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 4;

  public static final String MOVIES = "CREATE TABLE movies ("
   + MovieTables.MovieColumns._ID + " INTEGER PRIMARY KEY,"
   + MovieTables.MovieColumns.TITLE + " TEXT NOT NULL,"
   + MovieTables.MovieColumns.POSTER + " TEXT NOT NULL,"
   + MovieTables.MovieColumns.SYNOPSIS + " TEXT NOT NULL,"
   + MovieTables.MovieColumns.RATING + " REAL NOT NULL,"
   + MovieTables.MovieColumns.DATE + " TEXT)";

  public static final String TRAILERS = "CREATE TABLE trailers ("
   + MovieTables.TrailerColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
   + MovieTables.TrailerColumns.MOVIE_ID + " INTEGER NOT NULL REFERENCES movies(_id),"
   + MovieTables.TrailerColumns.VIDEO + " TEXT NOT NULL)";

  public static final String REVIEWS = "CREATE TABLE reviews ("
   + MovieTables.ReviewColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
   + MovieTables.ReviewColumns.MOVIE_ID + " INTEGER NOT NULL REFERENCES movies(_id),"
   + MovieTables.ReviewColumns.AUTHOR + " TEXT NOT NULL,"
   + MovieTables.ReviewColumns.CONTENT + " TEXT NOT NULL UNIQUE)";

  private static volatile MovieDatabase instance;

  private Context context;

  private MovieDatabase(Context context) {
    super(context, "movieDatabase.db", null, DATABASE_VERSION);
    this.context = context;
  }

  public static MovieDatabase getInstance(Context context) {
    if (instance == null) {
      synchronized (MovieDatabase.class) {
        if (instance == null) {
          instance = new MovieDatabase(context);
        }
      }
    }
    return instance;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(MOVIES);
    db.execSQL(TRAILERS);
    db.execSQL(REVIEWS);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
