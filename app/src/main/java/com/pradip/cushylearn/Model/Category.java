package com.pradip.cushylearn.Model;

/**
 * Created by rbaisak on 12/14/16.
 */

public class Category {
    private String cat_name;
    private int cat_status;
    private long cat_parent;
    private long id;
    private String cat_slug;


    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public int getCat_status() {
        return cat_status;
    }

    public void setCat_status(int cat_status) {
        this.cat_status = cat_status;
    }

    public long getCat_parent() {
        return cat_parent;
    }

    public void setCat_parent(long cat_parent) {
        this.cat_parent = cat_parent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCat_slug() {
        return cat_slug;
    }

    public void setCat_slug(String cat_slug) {
        this.cat_slug = cat_slug;
    }

    public String toString()
    {
        return( cat_name );
    }
}
