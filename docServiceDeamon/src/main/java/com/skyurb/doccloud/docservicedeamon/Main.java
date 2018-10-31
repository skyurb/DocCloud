package com.skyurb.doccloud.docservicedeamon;

import com.skyurb.doccloud.job.JobDaemonService;
import com.skyurb.doccloud.job.JobDaemonServiceImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JobDaemonServiceImpl instance = new JobDaemonServiceImpl();
        new Thread(instance).start();
        // 创建一个RPC builder
        RPC.Builder builder = new RPC.Builder(new Configuration());
        //指定RPC Server的参数
        builder.setBindAddress("localhost");
        builder.setPort(7788);
        //将自己的程序部署到server上
        builder.setProtocol(JobDaemonService.class);
        builder.setInstance(instance);
        //创建Server
        RPC.Server server = builder.build();

        //启动服务
        server.start();
    }
}
