package com.example.madelenko.app.moviegami.datalayer.generated.values;

import android.content.ContentValues;
import com.example.madelenko.app.moviegami.datalayer.MovieTables;
import java.lang.String;

public class ReviewsValuesBuilder {
  ContentValues values = new ContentValues();

  public ReviewsValuesBuilder Id(int value) {
    values.put(MovieTables.ReviewColumns._ID, value);
    return this;
  }

  public ReviewsValuesBuilder Id(long value) {
    values.put(MovieTables.ReviewColumns._ID, value);
    return this;
  }

  public ReviewsValuesBuilder movieId(int value) {
    values.put(MovieTables.ReviewColumns.MOVIE_ID, value);
    return this;
  }

  public ReviewsValuesBuilder movieId(long value) {
    values.put(MovieTables.ReviewColumns.MOVIE_ID, value);
    return this;
  }

  public ReviewsValuesBuilder author(String value) {
    values.put(MovieTables.ReviewColumns.AUTHOR, value);
    return this;
  }

  public ReviewsValuesBuilder content(String value) {
    values.put(MovieTables.ReviewColumns.CONTENT, value);
    return this;
  }

  public ContentValues values() {
    return values;
  }
}
