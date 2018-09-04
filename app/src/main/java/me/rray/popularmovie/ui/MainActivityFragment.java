package me.rray.popularmovie.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.rray.popularmovie.R;
import me.rray.popularmovie.data.Movie;


public class MainActivityFragment extends Fragment {


    private MoviePosterGridAdapter mMoviePosterGridAdapter;

    private static final String SORT_SETTING_KEY = "sort_setting";
    private static final String POPULARITY_DESC = "popularity.desc";
    private static final String RATING_DESC = "vote_average.desc";
    private static final String MOVIES_KEY = "movies";

    private String sortBy = POPULARITY_DESC;

    private ArrayList<Movie> movies = null;

    @BindView(R.id.gridview_movie_poster) GridView mGridView;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);

        if (sortBy.equals(POPULARITY_DESC)) {
            if (!action_sort_by_popularity.isChecked())
                action_sort_by_popularity.setChecked(true);
        } else {
            if (!action_sort_by_rating.isChecked())
                action_sort_by_rating.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getGroupId();

        switch (id) {
            case R.id.action_sort_by_popularity:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                sortBy = POPULARITY_DESC;
                //updateMovies(sortBy);
                return true;
            case R.id.action_sort_by_rating:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                sortBy = RATING_DESC;
                //updateMovies(sortBy);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);

        //GridView mGridView = view.findViewById(R.id.gridview_movie_poster);
        ButterKnife.bind(getActivity(), view);

        mMoviePosterGridAdapter = new MoviePosterGridAdapter(getActivity());
        mGridView.setAdapter(mMoviePosterGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMoviePosterGridAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra(MovieDetailActivityFragment.DETAIL_MOVIE, movie);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                sortBy = savedInstanceState.getString(SORT_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                if (movies != null) {
                    for (Movie movie : movies) {
                        mMoviePosterGridAdapter.add(movie);
                    }
                }
            } else {
                updateMovie(sortBy);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (!sortBy.contentEquals(POPULARITY_DESC)) {
            outState.putString(SORT_SETTING_KEY, sortBy);
        }

        if (movies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, movies);
        }
        super.onSaveInstanceState(outState);
    }

    //update movie through FetchMovieData class
    private void updateMovie(String sortBy) {
        FetchMovieData movieData = new FetchMovieData();
        movieData.execute(sortBy);
    }


    //Async Task
    public class FetchMovieData extends AsyncTask<String, Void, List<Movie>> {

        //tried WeakReference to resolve memory leak issue
        //private WeakReference<MainActivityFragment> mMainActivityFragmentRef;
        //private WeakReference<Context> mContextRef;
        private final String LOG_TAG = FetchMovieData.class.getSimpleName();
        private final String API_KEY = "874bc7ae849581642d7bf8e96a9016bb";

        FetchMovieData() {
            //mMainActivityFragmentRef = new WeakReference<>(mainActivityFragment);
            //mContextRef = new WeakReference<>(context);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            if (strings == null || strings.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr;

            final String BASE_URL = "http://image.tmdb.org/t/p/";
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            try {
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, strings[0])
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder sb = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                if (sb.length() == 0) {
                    return null;
                }
                jsonStr = sb.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "error closing. ", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing JSON", e);
                e.printStackTrace();
            }

            return null;
        }

        //method getMoviesDateFromJson: parse JSON string into list of Movie objects
        private List<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
            // "results": [
            //    {
            //      "vote_count": 7266,
            //      "id": 299536,
            //      "video": false,
            //      "vote_average": 8.3,
            //      "title": "Avengers: Infinity War",
            //      "popularity": 281.682,
            //      "poster_path": "/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg",
            //      "original_language": "en",
            //      "original_title": "Avengers: Infinity War",
            //      "genre_ids": [
            //        12,
            //        878,
            //        14,
            //        28
            //      ],
            //      "backdrop_path": "/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg",
            //      "adult": false,
            //      "overview": "As the Avengers and their allies have continued to protect the world from threats too large for any one hero to handle, a new danger has emerged from the cosmic shadows: Thanos. A despot of intergalactic infamy, his goal is to collect all six Infinity Stones, artifacts of unimaginable power, and use them to inflict his twisted will on all of reality. Everything the Avengers have fought for has led up to this moment - the fate of Earth and existence itself has never been more uncertain.",
            //      "release_date": "2018-04-25"
            //    },
            //    { ...

            JSONObject movieObject = new JSONObject(jsonStr);
            JSONArray movieArray = movieObject.getJSONArray("results");

            List<Movie> results = new ArrayList<>();

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieJsonObject = movieArray.getJSONObject(i);
                Movie movieModel = new Movie(movieJsonObject);
                results.add(movieModel);
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<Movie> m) {

            if (m != null) {
                if (mMoviePosterGridAdapter != null){
                    mMoviePosterGridAdapter.clear();
                    for (Movie movie : m) {
                        mMoviePosterGridAdapter.add(movie);
                    }
                }
                movies = new ArrayList<>();
                movies.addAll(m);
            }
        }
    }

}
