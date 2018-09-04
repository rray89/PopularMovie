package me.rray.popularmovie.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.rray.popularmovie.R;
import me.rray.popularmovie.data.Movie;

public class MoviePosterGridAdapter extends ArrayAdapter<Movie>{


    //View holder for ArrayAdapter
    static class ViewHolder {
        //@BindView(R.id.iv_movie_poster_image) ImageView moviePosterImageView;
        //@BindView(R.id.tv_movie_poster_title) TextView moviePosterTitleView;
        final ImageView moviePosterImageView;
        final TextView moviePosterTitleView;

        ViewHolder (View view) {
            moviePosterImageView = view.findViewById(R.id.iv_movie_poster_image);
            moviePosterTitleView = view.findViewById(R.id.tv_movie_poster_title);

        }
    }



    MoviePosterGridAdapter(Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        //ImageView imageView = getView(position);
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_movie_poster,
                    parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Movie movie = getItem(position);
        String imageUrl = "http://image.tmdb.org/t/p/" + movie.getPoster();

        viewHolder = (ViewHolder) view.getTag();

        Picasso.get().load(imageUrl).into(viewHolder.moviePosterImageView);

        return view;
    }
}
