package com.simonjamesrowe.backend.entrypoints.scheduled;

public interface ICmsSynchronization {
    void syncBlogDocuments();
    void syncSiteDocuments();
}