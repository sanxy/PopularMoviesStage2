package com.sanxynet.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TrailerList implements Serializable, Parcelable {

    public final static Creator<TrailerList> CREATOR = new Creator<TrailerList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrailerList createFromParcel(Parcel in) {
            return new TrailerList(in);
        }

        public TrailerList[] newArray(int size) {
            return (new TrailerList[size]);
        }

    };
    private final static long serialVersionUID = 3114427386563028848L;
    @SerializedName("id")
    @Expose
    private final Integer id;
    @SerializedName("results")
    @Expose
    private final List<Trailer> results = null;

    private TrailerList(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.results, (Trailer.class.getClassLoader()));
    }

    public List<Trailer> getResults() {
        return results;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}

