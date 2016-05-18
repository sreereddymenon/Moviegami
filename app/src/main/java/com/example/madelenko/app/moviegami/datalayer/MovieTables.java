package com.example.madelenko.app.moviegami.datalayer;


import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Unique;


/* Class containing an Interface for each table. each of the statements within
   the interface represents a column in the table. Each column definition is
   preceded by annotations defining datatype and other field modifiers like
   "primary key" and "not null"
 */
public class MovieTables {

    public interface MovieColumns {

        @DataType(DataType.Type.INTEGER) @PrimaryKey
        String _ID = "_id";

        @DataType(DataType.Type.TEXT) @NotNull
        String TITLE = "title";

        @DataType(DataType.Type.TEXT) @NotNull
        String POSTER = "poster";

        @DataType(DataType.Type.TEXT) @NotNull
        String SYNOPSIS = "synopsis";

        @DataType(DataType.Type.REAL) @NotNull
        String RATING = "rating";

        @DataType(DataType.Type.TEXT) @Nullable
        String DATE = "date";
    }

    public interface  TrailerColumns {

        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
        String _ID = "_id";

        @DataType(DataType.Type.INTEGER) @NotNull
        @References(table=MovieDatabase.MOVIES, column=MovieColumns._ID)
        String MOVIE_ID = "movie_id";

        @DataType(DataType.Type.TEXT) @NotNull
        String VIDEO = "video";
    }

    public interface  ReviewColumns {

        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
        String _ID = "_id";

        @DataType(DataType.Type.INTEGER) @NotNull
        @References(table = MovieDatabase.MOVIES, column = MovieColumns._ID)
        String MOVIE_ID = "movie_id";

        @DataType(DataType.Type.TEXT) @NotNull
        String AUTHOR = "author";

        @DataType(DataType.Type.TEXT) @NotNull @Unique
        String CONTENT = "content";
    }
}
