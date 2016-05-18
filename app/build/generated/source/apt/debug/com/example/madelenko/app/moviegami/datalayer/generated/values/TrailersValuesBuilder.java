package com.example.madelenko.app.moviegami.datalayer.generated.values;

import android.content.ContentValues;
import com.example.madelenko.app.moviegami.datalayer.MovieTables;
import java.lang.String;

public class TrailersValuesBuilder {
  ContentValues values = new ContentValues();

  public TrailersValuesBuilder Id(int value) {
    values.put(MovieTables.TrailerColumns._ID, value);
    return this;
  }

  public TrailersValuesBuilder Id(long value) {
    values.put(MovieTables.TrailerColumns._ID, value);
    return this;
  }

  public TrailersValuesBuilder movieId(int value) {
    values.put(MovieTables.TrailerColumns.MOVIE_ID, value);
    return this;
  }

  public TrailersValuesBuilder movieId(long value) {
    values.put(MovieTables.TrailerColumns.MOVIE_ID, value);
    return this;
  }

  public TrailersValuesBuilder video(String value) {
    values.put(MovieTables.TrailerColumns.VIDEO, value);
    return this;
  }

  public ContentValues values() {
    return values;
  }
}
