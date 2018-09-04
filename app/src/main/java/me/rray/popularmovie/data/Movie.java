package me.rray.popularmovie.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable{

    private int id;
    private String title;
    private String poster;
    private String backDrop;
    private String overview;
    private double userRating;
    private String releaseDate;

    // empty constructor
    public Movie() { }

    public Movie(JSONObject movie) throws JSONException {
        this.title = movie.getString("id");
        this.title = movie.getString("title");
        this.poster = movie.getString("poster_path");
        this.backDrop = movie.getString("backdrop_path");
        this.overview = movie.getString("overview");
        this.userRating = movie.getDouble("vote_average");
        this.releaseDate = movie.getString("release_date");
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster = in.readString();
        backDrop = in.readString();
        overview = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public int getId () {
        return id;
    }

    public String getTitle () {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getBackDrop() {
        return backDrop;
    }

    public String getOverview() {
        return overview;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    //    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(backDrop);
        dest.writeString(overview);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
    }
}
