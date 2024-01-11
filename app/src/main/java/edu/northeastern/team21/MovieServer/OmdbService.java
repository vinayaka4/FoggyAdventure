package edu.northeastern.team21.MovieServer;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface OmdbService {
    @GET("?apikey=b12f3f4e")
    Observable<OmdbResult> getMovie(@QueryMap Map<String, String> filters);

}
