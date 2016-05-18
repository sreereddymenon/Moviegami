package com.example.madelenko.app.moviegami.datalayer;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


/* Content provider generator code. Implemented using the "schematic" library.
   This class generates a content provider in the build directory when the assemble
   task is called. Each inner static class contains URI's that can be called to perform
   CRUD operations.

 */
@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {

    public static final String AUTHORITY =
            "com.example.madelenko.app.moviegami.datalayer.MovieProvider";

    @TableEndpoint(table = MovieDatabase.MOVIES)
    public static class Movies {

        @ContentUri(
                path = "movies",
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieTables.MovieColumns.TITLE + " ASC")
        public static final Uri MOVIES = Uri.parse("content://" + AUTHORITY + "/movies");

    }

    @TableEndpoint(table = MovieDatabase.TRAILERS)
    public static class Trailers {

        @InexactContentUri(
                path = MovieDatabase.MOVIES + "/#/" + MovieDatabase.TRAILERS,
                name = "TRAILER_LIST",
                type = "vnd.android.cursor.dir/trailer",
                whereColumn = MovieTables.TrailerColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withIdTrailers(long id) {
            return Uri.withAppendedPath(Movies.MOVIES, String.valueOf(id))
                    .buildUpon()
                    .appendPath(MovieDatabase.TRAILERS)
                    .build();
        }
    }

    @TableEndpoint(table = MovieDatabase.REVIEWS)
    public static class Reviews {

        @InexactContentUri(
                path = MovieDatabase.MOVIES + "/#/" + MovieDatabase.REVIEWS,
                name = "REVIEW_LIST",
                type = "vnd.android.cursor.dir/review",
                whereColumn = MovieTables.ReviewColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withIdReviews(long id) {
            return Uri.withAppendedPath(Movies.MOVIES, String.valueOf(id))
                    .buildUpon()
                    .appendPath(MovieDatabase.REVIEWS)
                    .build();
        }
    }
}