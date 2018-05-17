package com.pradip.cushylearn.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rbaisak on 15/2/17.
 */

public class ItemNameList implements Serializable {

    String Item_List;

    private List<String> ItemimageURL;

    public ItemNameList(String itemName) {
        this.Item_List = itemName;
    }

    public String getItem_List() {
        return Item_List;
    }

    public void setItem_List(String item_List) {
        this.Item_List = item_List;
    }

    public List<String> getItemimageURL() {
        return ItemimageURL;
    }

    public void setItemimageURL(List<String> itemimageURL) {
        ItemimageURL = itemimageURL;
    }







}