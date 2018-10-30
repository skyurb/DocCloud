package com.skyurb.doccloud.job;

import org.apache.hadoop.ipc.ProtocolSignature;

import java.io.IOException;

/*
*@ClassName:JobDaemonServiceImpl
 @Description:TODO
 @Author:
 @Date:2018/10/30 10:56 
 @Version:v1.0
*/
public class JobDaemonServiceImpl implements JobDaemonService {
    public void submitDocJob(DocJob job) {
        System.out.println(job);
    }

    public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
        return versionID;
    }

    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return null;
    }
}
