package com.flowertech.taskplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "categories")
public class Category implements Serializable {

    public Category() {
        this.created = new Date();
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public Date created;

    @NonNull
    public String title;

    public String description;

    @NonNull
    @ColumnInfo(name = "abbreviation")
    public String abbr;
}
