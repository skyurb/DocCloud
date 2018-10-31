package com.skyurb.doccloud.doccloudweb.controller;

import com.skyurb.doccloud.doccloudweb.entity.Doc;
import com.skyurb.doccloud.doccloudweb.service.DocService;
import com.skyurb.doccloud.doccloudweb.util.HdfsUtil;
import com.skyurb.doccloud.doccloudweb.util.MD5Util;
import com.skyurb.doccloud.job.DocJob;
import com.skyurb.doccloud.job.DocJobType;
import com.skyurb.doccloud.job.JobDaemonService;
import com.skyurb.doccloud.job.JobStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/doc")
public class DocUploadController {
    @Autowired
    private DocService docService;
    public static final String[] DOC_SUFFIXS = new String[]{"doc", "docx", "ppt", "pptx", "txt", "xls", "xlsx", "pdf"};
    public static final int DOC_MAX_SIZE = 128 * 1024 * 1024;
    public static final String HOME="hdfs://192.168.203.74:9000/doccloud/";


    @RequestMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file){
        //判断是否为空
        if (file.isEmpty()){
            return "file is empty";
        }
        //判断后缀是否合法
        String filename = file.getOriginalFilename();
        String[] splits = filename.split("\\.");
        String suffix = splits[1];
        boolean flag = isSuffixLegal(suffix);
        if (!flag){
            return "file is illegal";
        }


        try {
            //判断文件大小是否符合标准
            byte[] bytes = file.getBytes();
            if (bytes.length > DOC_MAX_SIZE) {
                return "file is large,max size is " + DOC_MAX_SIZE;
            }
            //计算文档md5值
            String md5 = getMd5(bytes);

            //从数据库校验md5
            //log.info("file md5 is {}",md5);
            Optional<Doc> doc = docService.findByMd5(md5);
            if (doc.isPresent()){
                Doc docEntity = doc.get();
                //没有登录，先模拟数据
                docEntity.setUserId(new Random().nextInt());
                //docRepository.save(docEntity);
                docService.save(docEntity);
                //docService.
            }else{
                //文库中没有此文档 需要上传
                //生成文件存放目录路径
                //获取当前日期
                String date=getDate();
                String dst=HOME+"/"+date+"/"+UUID.randomUUID().toString()+"/";
                log.info("dst {}",dst);
                HdfsUtil.upload(bytes,file.getOriginalFilename(),dst);
                //保存文档元数据
                Doc docEntity = new Doc();
                docEntity.setUserId(new Random().nextInt());
                docEntity.setDocComment("hadoop");
                docEntity.setDocDir(dst);
                docEntity.setDocName(filename);
                docEntity.setDocSize(bytes.length);
                docEntity.setDocPermission("1");
                docEntity.setDocType(suffix);
                docEntity.setDocStatus("upload");
                docEntity.setMd5(md5);
                docEntity.setDocCreateTime(new Date());
                docService.save(docEntity);
                //上传成功以后需要提交文档转换任务
                //转换成html,
                submitDocJob(docEntity,new Random().nextInt());
                //转换成pdf提取缩略图，页数
                //提取文本 建立索引

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "upload success";
    }

    private void submitDocJob(Doc docEntity, int userId) throws IOException {
        DocJob docJob;
        docJob = new DocJob();
        docJob.setName("doc convert");
        //设置文件路径
        docJob.setInput(docEntity.getDocDir()+"/"+docEntity.getDocName());
        docJob.setOutput(docEntity.getDocDir());
        docJob.setUserId(userId);
        docJob.setSubmitTime(System.nanoTime());
        docJob.setRetryTime(2);
        docJob.setFileName(docEntity.getDocName());
        docJob.setJobStatus(JobStatus.SUBMIT);
        docJob.setJobType(DocJobType.DOC_JOB_CONVERT);
        //保存job元数据，防止任务出错
        JobDaemonService jobDaemonService = RPC.getProxy(JobDaemonService.class, 1L, new InetSocketAddress("localhost", 7788), new Configuration());
        log.info("submit job:{}",docJob);
        jobDaemonService.submitDocJob(docJob);
    }

    //获取当前日期
    private String getDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
    //获取文件的md5
    private String getMd5(byte[] bytes) {
        return MD5Util.getMD5String(bytes);

    }

    private boolean isSuffixLegal(String suffix) {
        for (String docSuffix :
                DOC_SUFFIXS) {
            if (suffix.equals(docSuffix)) {
                return true;
            }
        }
        return false;
    }

}
