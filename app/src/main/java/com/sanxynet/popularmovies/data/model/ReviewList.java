package com.sanxynet.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReviewList implements Serializable, Parcelable {

    public final static Creator<ReviewList> CREATOR = new Creator<ReviewList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ReviewList createFromParcel(Parcel in) {
            return new ReviewList(in);
        }

        public ReviewList[] newArray(int size) {
            return (new ReviewList[size]);
        }

    };
    private final static long serialVersionUID = -3339513292394888707L;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private final Integer page;
    @SerializedName("results")
    @Expose
    private final List<Review> results = null;
    @SerializedName("total_pages")
    @Expose
    private final Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private final Integer totalResults;

    private ReviewList(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.results, (Review.class.getClassLoader()));
        this.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalResults = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public List<Review> getResults() {
        return results;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(page);
        dest.writeList(results);
        dest.writeValue(totalPages);
        dest.writeValue(totalResults);
    }

    public int describeContents() {
        return 0;
    }

}
