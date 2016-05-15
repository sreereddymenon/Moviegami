package com.example.madelenko.app.moviegami.datalayer;


import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {

    public static final int VERSION = 4;

    @Table(MovieTables.MovieColumns.class) public static final String MOVIES = "movies";
    @Table(MovieTables.TrailerColumns.class) public static final String TRAILERS = "trailers";
    @Table(MovieTables.ReviewColumns.class) public static final String REVIEWS = "reviews";
}