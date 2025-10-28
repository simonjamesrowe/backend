package com.simonjamesrowe.searchservice.entrypoints.scheduled;

public interface ICmsSynchronization {
    void syncBlogDocuments();
    void syncSiteDocuments();
}