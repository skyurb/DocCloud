package com.skyurb.doccloud.job;


import org.apache.hadoop.ipc.VersionedProtocol;

public interface JobDaemonService extends VersionedProtocol {
    long versionID=1L;
    void submitDocJob(DocJob job);
}
