package com.sanxynet.popularmovies.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Trailer implements Serializable, Parcelable {

        public final static Creator<Trailer> CREATOR = new Creator<Trailer>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return (new Trailer[size]);
        }

    };
    private final static long serialVersionUID = 7447683429346275855L;
    @SerializedName("id")
    @Expose
    private final String id;
    @SerializedName("key")
    @Expose
    private final String key;
    @SerializedName("name")
    @Expose
    private final String name;


    private Trailer(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.key = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(key);
        dest.writeValue(name);
    }

    public int describeContents() {
        return 0;
    }

}
