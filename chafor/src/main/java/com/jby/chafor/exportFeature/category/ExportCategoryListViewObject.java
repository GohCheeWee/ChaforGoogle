package com.jby.chafor.exportFeature.category;

/**
 * Created by user on 11/5/2017.
 */

public class ExportCategoryListViewObject {
    private String id;
    private String category;
    private String subCategory_numb;

    public ExportCategoryListViewObject(String id, String category, String subCategory_numb) {
        this.id = id;
        this.category = category;
        this.subCategory_numb = subCategory_numb;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    String getSubCategory_numb() {
        return subCategory_numb;
    }
}
