package com.sanxynet.popularmovies.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public class PopularMoviesUtilities {

    public final static String TMDB_API_THUMBNAIL_PATH = "http://image.tmdb.org/t/p/w500";


    public static void launchTrailerVideoInYoutubeApp(@NonNull Context context, String videoID) {
        // Create the intent to launch the activity to show this trailer:
        Intent launchTrailerVideoInYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse(("vnd.youtube://" + videoID)));

        // Launch the Youtube app to watch the movie trailer:
        context.startActivity(launchTrailerVideoInYoutube);
    }

    private static final String BASE_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    public static void launchTrailerVideoInYoutubeBrowser(@NonNull Context context, String videoID) {
        // Create the intent to launch the activity to show this trailer
        Intent launchTrailerVideoInYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse((BASE_URL_YOUTUBE + videoID)));

        // Launch the Youtube app to watch the movie trailer
        context.startActivity(launchTrailerVideoInYoutube);
    }
}
