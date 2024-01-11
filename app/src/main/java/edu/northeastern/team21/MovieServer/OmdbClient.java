package edu.northeastern.team21.MovieServer;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class OmdbClient {

    private static final String OMDB_BASE_URL = "http://www.omdbapi.com/";

    private static OmdbClient instance;
    private OmdbService omdbService;

    private OmdbClient() {
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(OMDB_BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        omdbService = retrofit.create(OmdbService.class);
    }

    public static OmdbClient getInstance() {
        if (instance == null) {
            instance = new OmdbClient();
        }
        return instance;
    }

    public Observable<OmdbResult> getMovie(Map<String, String> filters) {
        return omdbService.getMovie(filters);
    }
}
