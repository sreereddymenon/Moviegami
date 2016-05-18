package com.example.madelenko.app.moviegami.datalayer.generated;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.lang.IllegalArgumentException;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import net.simonvt.schematic.utils.SelectionBuilder;

public class MovieProvider extends ContentProvider {
  public static final String AUTHORITY = "com.example.madelenko.app.moviegami.datalayer.MovieProvider";

  private static final int MOVIES_MOVIES = 0;

  private static final int TRAILERS_TRAILER_LIST = 1;

  private static final int REVIEWS_REVIEW_LIST = 2;

  private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    MATCHER.addURI(AUTHORITY, "movies", MOVIES_MOVIES);
    MATCHER.addURI(AUTHORITY, "movies/#/trailers", TRAILERS_TRAILER_LIST);
    MATCHER.addURI(AUTHORITY, "movies/#/reviews", REVIEWS_REVIEW_LIST);
  }

  private SQLiteOpenHelper database;

  @Override
  public boolean onCreate() {
    database = MovieDatabase.getInstance(getContext());
    return true;
  }

  private SelectionBuilder getBuilder(String table) {
    SelectionBuilder builder = new SelectionBuilder();
    return builder;
  }

  private long[] insertValues(SQLiteDatabase db, String table, ContentValues[] values) {
    long[] ids = new long[values.length];
    for (int i = 0; i < values.length; i++) {
      ContentValues cv = values[i];
      db.insertOrThrow(table, null, cv);
    }
    return ids;
  }

  @Override
  public int bulkInsert(Uri uri, ContentValues[] values) {
    final SQLiteDatabase db = database.getWritableDatabase();
    db.beginTransaction();
    try {
      switch(MATCHER.match(uri)) {
        case MOVIES_MOVIES: {
          long[] ids = insertValues(db, "movies", values);
          getContext().getContentResolver().notifyChange(uri, null);
          break;
        }
        case TRAILERS_TRAILER_LIST: {
          long[] ids = insertValues(db, "trailers", values);
          getContext().getContentResolver().notifyChange(uri, null);
          break;
        }
        case REVIEWS_REVIEW_LIST: {
          long[] ids = insertValues(db, "reviews", values);
          getContext().getContentResolver().notifyChange(uri, null);
          break;
        }
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
    return values.length;
  }

  @Override
  public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> ops) throws OperationApplicationException {
    ContentProviderResult[] results;
    final SQLiteDatabase db = database.getWritableDatabase();
    db.beginTransaction();
    try {
      results = super.applyBatch(ops);
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
    return results;
  }

  @Override
  public String getType(Uri uri) {
    switch(MATCHER.match(uri)) {
      case MOVIES_MOVIES: {
        return "vnd.android.cursor.dir/movie";
      }
      case TRAILERS_TRAILER_LIST: {
        return "vnd.android.cursor.dir/trailer";
      }
      case REVIEWS_REVIEW_LIST: {
        return "vnd.android.cursor.dir/review";
      }
      default: {
        throw new IllegalArgumentException("Unknown URI " + uri);
      }
    }
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    final SQLiteDatabase db = database.getReadableDatabase();
    switch(MATCHER.match(uri)) {
      case MOVIES_MOVIES: {
        SelectionBuilder builder = getBuilder("Movies");
        if (sortOrder == null) {
          sortOrder = "title ASC";
        }
        final String groupBy = null;
        final String having = null;
        final String limit = null;
        Cursor cursor = builder.table("movies")
            .where(selection, selectionArgs)
            .query(db, projection, groupBy, having, sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
      }
      case TRAILERS_TRAILER_LIST: {
        SelectionBuilder builder = getBuilder("Trailers");
        final String groupBy = null;
        final String having = null;
        final String limit = null;
        Cursor cursor = builder.table("trailers")
            .where("movie_id=?" , uri.getPathSegments().get(1))
            .where(selection, selectionArgs)
            .query(db, projection, groupBy, having, sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
      }
      case REVIEWS_REVIEW_LIST: {
        SelectionBuilder builder = getBuilder("Reviews");
        final String groupBy = null;
        final String having = null;
        final String limit = null;
        Cursor cursor = builder.table("reviews")
            .where("movie_id=?" , uri.getPathSegments().get(1))
            .where(selection, selectionArgs)
            .query(db, projection, groupBy, having, sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
      }
      default: {
        throw new IllegalArgumentException("Unknown URI " + uri);
      }
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = database.getWritableDatabase();
    switch(MATCHER.match(uri)) {
      case MOVIES_MOVIES: {
        final long id = db.insertOrThrow("movies", null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
      }
      case TRAILERS_TRAILER_LIST: {
        final long id = db.insertOrThrow("trailers", null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
      }
      case REVIEWS_REVIEW_LIST: {
        final long id = db.insertOrThrow("reviews", null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
      }
      default: {
        throw new IllegalArgumentException("Unknown URI " + uri);
      }
    }
  }

  @Override
  public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
    final SQLiteDatabase db = database.getWritableDatabase();
    switch(MATCHER.match(uri)) {
      case MOVIES_MOVIES: {
        SelectionBuilder builder = getBuilder("Movies");
        builder.where(where, whereArgs);
        final int count = builder.table("movies")
            .update(db, values);
        if (count > 0) {
          getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
      }
      case TRAILERS_TRAILER_LIST: {
        SelectionBuilder builder = getBuilder("Trailers");
        builder.where("movie_id=?", uri.getPathSegments().get(1));
        builder.where(where, whereArgs);
        final int count = builder.table("trailers")
            .update(db, values);
        if (count > 0) {
          getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
      }
      case REVIEWS_REVIEW_LIST: {
        SelectionBuilder builder = getBuilder("Reviews");
        builder.where("movie_id=?", uri.getPathSegments().get(1));
        builder.where(where, whereArgs);
        final int count = builder.table("reviews")
            .update(db, values);
        if (count > 0) {
          getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
      }
      default: {
        throw new IllegalArgumentException("Unknown URI " + uri);
      }
    }
  }

  @Override
  public int delete(Uri uri, String where, String[] whereArgs) {
    final SQLiteDatabase db = database.getWritableDatabase();
    switch(MATCHER.match(uri)) {
      case MOVIES_MOVIES: {
        SelectionBuilder builder = getBuilder("Movies");
        builder.where(where, whereArgs);
        final int count = builder
            .table("movies")
            .delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
      }
      case TRAILERS_TRAILER_LIST: {
        SelectionBuilder builder = getBuilder("Trailers");
        builder.where("movie_id=?", uri.getPathSegments().get(1));
        builder.where(where, whereArgs);
        final int count = builder
            .table("trailers")
            .delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
      }
      case REVIEWS_REVIEW_LIST: {
        SelectionBuilder builder = getBuilder("Reviews");
        builder.where("movie_id=?", uri.getPathSegments().get(1));
        builder.where(where, whereArgs);
        final int count = builder
            .table("reviews")
            .delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
      }
      default: {
        throw new IllegalArgumentException("Unknown URI " + uri);
      }
    }
  }
}
