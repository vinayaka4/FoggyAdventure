package edu.northeastern.team21;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.team21.MovieRV.MovieAdapter;
import edu.northeastern.team21.MovieServer.OmdbClient;
import edu.northeastern.team21.MovieServer.OmdbResult;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WebserviceActivity extends AppCompatActivity {

    private static final String TAG = "__WEBSERVER_ACTIVITY__";
    private TextInputLayout textInputLayoutKeyword;
    private Spinner spinnerType, spinnerYear;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter = new MovieAdapter();
    private TextView message;
    private ImageView loadingImg;
    private RotateAnimation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webservice);
        setTitle("Movie Search");

        anim = new RotateAnimation(0, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        anim.setRepeatCount(Animation.INFINITE);

        this.message = findViewById(R.id.errorTV);
        this.loadingImg = findViewById(R.id.loadingIV);

        this.textInputLayoutKeyword = findViewById(R.id.textInputLayoutKeyword);
        this.spinnerType = findViewById(R.id.spinnerType);
        this.spinnerYear = findViewById(R.id.spinnerYear);

        String[] typeOptions = {"All", "movie", "series", "episode"};
        List<String> YOptions = new ArrayList<>();
        YOptions.add("All");
        for (int i = Year.now().getValue(); i >= 1900; i--)
            YOptions.add(String.valueOf(i));

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, typeOptions);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, YOptions);

        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerType.setAdapter(adapterType);
        this.spinnerYear.setAdapter(adapterYear);


        recyclerView = findViewById(R.id.recyclerMovie);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("SetTextI18n")
    public void searchMovie(View view) {
        this.message.setText("");

        String keyword = Objects.requireNonNull(this.textInputLayoutKeyword.getEditText()).getText().toString();
        String type = this.spinnerType.getSelectedItem().toString();
        String year = this.spinnerYear.getSelectedItem().toString();

        if (keyword.isEmpty()) {
            this.message.setText("Please enter keyword!");
            return;
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("s", keyword);
        if (!type.equals("All")) {
            filters.put("type", type);
        }
        if (!year.equals("All")) {
            filters.put("y", year);
        }
        showLoading();
        getMovies(filters);
    }

    private void getMovies(Map<String, String> filters) {
        OmdbClient.getInstance().getMovie(filters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OmdbResult>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "In subscribe");
                    }

                    @Override
                    public void onNext(@NonNull OmdbResult result) {
                        hideLoading();
                        if (result.response) {
                            Log.d(TAG, "on next " + result.search.size() + "  " + result.totalResults);
                            movieAdapter.setMovies(result.search);
                        } else {
                            Log.d(TAG, "on next" + result.error);
                            movieAdapter.clearMovies();
                            message.setText(result.error);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "error");
                        e.printStackTrace();
                        hideLoading();
                        message.setText("Something wrong!");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                    }
                });
    }

    private void showLoading() {
        this.loadingImg.setVisibility(View.VISIBLE);
        this.loadingImg.startAnimation(anim);
    }

    private void hideLoading() {
        this.loadingImg.setVisibility(View.GONE);
        this.loadingImg.clearAnimation();
    }

}
