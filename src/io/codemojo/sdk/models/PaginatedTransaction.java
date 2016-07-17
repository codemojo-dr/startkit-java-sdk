package io.codemojo.sdk.models;

import io.codemojo.sdk.facades.IPagination;

import java.util.List;

/**
 * Created by shoaib on 26/10/14.
 */
public class PaginatedTransaction<T> {

    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    private String next_page_url;
    private String prev_page_url;
    private int from;
    private int to;
    private List<T> data;

    private IPagination<T> pages;

    public int getTotal() {
        return total;
    }

    public int getItemsPerPage() {
        return per_page;
    }

    public int getCurrentPageNumber() {
        return current_page;
    }

    public int getLastPageNumber() {
        return last_page;
    }
    public int getCurrentCollectionStart() {
        return from;
    }

    public int getCurrentCollectionEnd() {
        return to;
    }

    public List<T> getData() {
        return data;
    }

    public void setPaginationHandler(IPagination<T> pages) {
        this.pages = pages;
    }

    public PaginatedTransaction<T> nextPage(){
        if(pages != null){
            return pages.next();
        }
        return null;
    }

    public PaginatedTransaction<T> prevPage(){
        if(pages != null){
            return pages.next();
        }
        return null;
    }
}
