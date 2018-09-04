package me.rray.popularmovie.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.rray.popularmovie.R;
import me.rray.popularmovie.data.Movie;


public class MovieDetailActivityFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getCanonicalName();
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    private static final String IMAGE_URL_BASE = "http://image.tmdb.org/t/p/";


    private Movie mMovie;

    @BindView(R.id.iv_movie_detail_image) ImageView mDetailImage;
    @BindView(R.id.tv_movie_detail_title) TextView mDetailTitle;
    @BindView(R.id.tv_movie_detail_overview) TextView mDetailOverview;
    @BindView(R.id.tv_movie_detail_date) TextView mDetailDate;
    @BindView(R.id.tv_movie_detail_vote_average) TextView mDetailVoteAverage;

//    private ImageView mDetailImage;
//    private TextView mDetailTitle;
//    private TextView mDetailOverview;
//    private TextView mDetailDate;
//    private TextView mDetailVoteAverage;

    public MovieDetailActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(MovieDetailActivityFragment.DETAIL_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail,
                container, false);
        ButterKnife.bind(getActivity(), rootView);

        String IMAGE_SIZE = "w342";

        String imageUrl = IMAGE_URL_BASE + IMAGE_SIZE + mMovie.getBackDrop();
        Picasso.get().load(imageUrl).into(mDetailImage);
        mDetailTitle.setText(mMovie.getTitle());
        mDetailOverview.setText(mMovie.getOverview());
        mDetailVoteAverage.setText(Double.toString(mMovie.getUserRating()));

        String movieReleaseDate = mMovie.getReleaseDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String date = DateUtils.formatDateTime(getActivity(),
                    format.parse(movieReleaseDate).getTime(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            mDetailDate.setText(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error while parsing. " + e);
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        return rootView;
    }
}
