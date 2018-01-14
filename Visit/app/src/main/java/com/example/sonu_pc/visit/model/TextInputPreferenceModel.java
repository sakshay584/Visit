package com.example.sonu_pc.visit.model;

import java.util.List;
import java.util.Map;

/**
 * Created by sonupc on 14-01-2018.
 */

public class TextInputPreferenceModel {

    private String page_title;
    private List<String> hints;

    public TextInputPreferenceModel() {
    }

    public TextInputPreferenceModel(String page_title, List<String> hints) {
        this.page_title = page_title;
        this.hints = hints;
    }

    public String getPage_title() {
        return page_title;
    }

    public void setPage_title(String page_title) {
        this.page_title = page_title;
    }

    public List<String> getHints() {
        return hints;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }
}
