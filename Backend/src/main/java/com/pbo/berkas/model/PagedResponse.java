package com.pbo.berkas.model;

import java.util.List;

public class PagedResponse<T> {
    public List<T> content;
    public long totalElements;
    public int totalPages;
    public int currentPage;

    public PagedResponse(List<T> content, long totalElements, int totalPages, int currentPage) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }
}
