package com.skyurb.doccloud.job;

import lombok.Data;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//此类封装job信息
@Data
public class DocJob implements Writable {
    private int id;
    private String name;
    private DocJobType jobType;
    private int userId;
    private long submitTime;
    private long finishTime;
    //任务状态
    private JobStatus jobStatus;
    //任务重试次数
    private int retryTime;
    //文档输入路径
    private String input;
    //任务输出路径
    private String output;


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(id);
        out.writeUTF(name);
        out.writeUTF(jobType.name());
        out.writeInt(userId);
        out.writeLong(finishTime);
        out.writeLong(submitTime);
        out.writeUTF(jobStatus.name());
        out.writeInt(retryTime);
        out.writeUTF(input);
        out.writeUTF(output);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id= in.readInt();
        name=in.readUTF();
        jobType=DocJobType.valueOf(in.readUTF());
        userId=in.readInt();
        finishTime=in.readLong();
        submitTime=in.readLong();
        jobStatus=JobStatus.valueOf(in.readUTF());
        retryTime=in.readInt();
        input=in.readUTF();
        output=in.readUTF();

    }
}
