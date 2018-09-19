package com.sanxynet.popularmovies.data.remote;

import com.sanxynet.popularmovies.data.model.MovieList;
import com.sanxynet.popularmovies.data.model.ReviewList;
import com.sanxynet.popularmovies.data.model.TrailerList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    String POPULARITY = "movie/popular";
    String RATING = "movie/top_rated";
    String API_KEY = "api_key";
    String TRAILERS = "movie/{movie_id}/videos";
    String REVIEWS = "movie/{movie_id}/reviews";

    @GET(POPULARITY)
    Call<MovieList> getMoviesByPopularity(@Query(API_KEY) String apiKey);

    @GET(RATING)
    Call<MovieList> getMoviesByUserRating(@Query(API_KEY) String apiKey);

    @GET(TRAILERS)
    Call<TrailerList> getTrailersByMovie(@Path("movie_id") int movie_id, @Query(API_KEY) String apiKey);

    @GET(REVIEWS)
    Call<ReviewList> getReviewsByMovie(@Path("movie_id") int movie_id, @Query(API_KEY) String apiKey);
}
