package com.example.madelenko.app.moviegami.datalayer.generated.values;

import android.content.ContentValues;
import com.example.madelenko.app.moviegami.datalayer.MovieTables;
import java.lang.String;

public class MoviesValuesBuilder {
  ContentValues values = new ContentValues();

  public MoviesValuesBuilder Id(int value) {
    values.put(MovieTables.MovieColumns._ID, value);
    return this;
  }

  public MoviesValuesBuilder Id(long value) {
    values.put(MovieTables.MovieColumns._ID, value);
    return this;
  }

  public MoviesValuesBuilder title(String value) {
    values.put(MovieTables.MovieColumns.TITLE, value);
    return this;
  }

  public MoviesValuesBuilder poster(String value) {
    values.put(MovieTables.MovieColumns.POSTER, value);
    return this;
  }

  public MoviesValuesBuilder synopsis(String value) {
    values.put(MovieTables.MovieColumns.SYNOPSIS, value);
    return this;
  }

  public MoviesValuesBuilder rating(float value) {
    values.put(MovieTables.MovieColumns.RATING, value);
    return this;
  }

  public MoviesValuesBuilder date(String value) {
    values.put(MovieTables.MovieColumns.DATE, value);
    return this;
  }

  public ContentValues values() {
    return values;
  }
}
