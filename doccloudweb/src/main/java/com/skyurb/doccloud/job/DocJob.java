package com.skyurb.doccloud.job;

import lombok.Data;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

//此类封装job信息
@Data
public class DocJob implements Writable,Serializable {
    private static final long serialVersionUID = 12345678L;
    private String id;
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
    //任务处理文件名
    private String fileName;
    //任务处理文档id
    private int docId;


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(name);
        out.writeUTF(jobType.name());
        out.writeInt(userId);
        out.writeLong(finishTime);
        out.writeLong(submitTime);
        out.writeUTF(jobStatus.name());
        out.writeInt(retryTime);
        out.writeUTF(input);
        out.writeUTF(output);
        out.writeUTF(fileName);
        out.writeInt(docId);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id= in.readUTF();
        name=in.readUTF();
        jobType=DocJobType.valueOf(in.readUTF());
        userId=in.readInt();
        finishTime=in.readLong();
        submitTime=in.readLong();
        jobStatus=JobStatus.valueOf(in.readUTF());
        retryTime=in.readInt();
        input=in.readUTF();
        output=in.readUTF();
        fileName=in.readUTF();
        docId=in.readInt();

    }
}
