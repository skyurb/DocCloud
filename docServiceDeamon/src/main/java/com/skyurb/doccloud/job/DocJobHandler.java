package com.skyurb.doccloud.job;

import com.skyurb.doccloud.job.callback.DocJobCallback;
import com.skyurb.doccloud.job.callback.DocJobResponse;
import com.skyurb.doccloud.util.FullTextIndexUtil;
import com.skyurb.doccloud.util.HdfsUtil;
import com.skyurb.doccloud.util.PdfUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

@Slf4j
public class DocJobHandler implements Runnable {
    private DocJob docJob;
    public DocJobHandler(DocJob docJob) {
        this.docJob=docJob;
        log.info("start to deal job {}",docJob);
    }

    public void run() {
        //得到输入路径
        String input = docJob.getInput();
        String tmpWorkDirPath = "/tmp/docjobdaemon/" + UUID.randomUUID().toString() + "/";
        //创建临时工作目录
        File tmpWorkDir = new File(tmpWorkDirPath);
        tmpWorkDir.mkdirs();
        //下载文件到临时目录
        try {
            HdfsUtil.download(input, tmpWorkDirPath);
            log.info("download file to {}",tmpWorkDirPath);
            //step1：文档转换成html
            convertToHtml(docJob.getFileName(),tmpWorkDir);
            //step2 转换成pdf
            convertToPdf(docJob.getFileName(),tmpWorkDir);

            Thread.sleep(2000);
            //step3 提取页码
            String pdfPath = tmpWorkDirPath + docJob.getFileName().substring(0, docJob.getFileName().indexOf(".")) + ".pdf";
            String htmlPath = tmpWorkDirPath  + docJob.getFileName().substring(0, docJob.getFileName().indexOf(".")) + ".html";
            String thumbnailsPath=tmpWorkDir.getAbsolutePath()+"/"+docJob.getFileName().substring(0,docJob.getFileName().indexOf("."))+".png";
            log.info("pdfpath:{}",pdfPath);
            int numberOfPages = PdfUtil.getNumberOfPages(pdfPath);
            log.info("numberOfPages:{}",numberOfPages);
            //step4 提取首页缩略图
            PdfUtil.getThumbnails(pdfPath,thumbnailsPath);
            //提取文档内容
            String content = PdfUtil.getContent(pdfPath);
            System.out.println(pdfPath+"............");
            //建立文档对象
            DocIndex docIndex = new DocIndex();
            log.info("begin create docObject");
            docIndex.setId(docJob.getDocId());
            docIndex.setDocName(docJob.getFileName());
            docIndex.setDocContent(content);
            docIndex.setUrl(docJob.getInput() + "/" + docJob.getFileName());
            String[] strings = docJob.getFileName().split("\\.");
            docIndex.setDocType(strings[1]);
            //step5 利用solr建立索引
            FullTextIndexUtil.add(docIndex);
            log.info("solr create success");
            //step6 上传结果
            HdfsUtil.copyFromLocal(htmlPath, docJob.getOutput());
            log.info("upload {} to hdfs:", htmlPath);
            HdfsUtil.copyFromLocal(pdfPath, docJob.getOutput());
            log.info("upload {} to hdfs:", pdfPath);
            HdfsUtil.copyFromLocal(thumbnailsPath, docJob.getOutput());
            log.info("upload {} to hdfs:", thumbnailsPath);

            //step7 清理临时目录
            log.info("clear tmpworkdir : {}",tmpWorkDir.getAbsolutePath());
           // FileUtils.deleteDirectory(tmpWorkDir);
            //step8 任务成功回调
            reportDocJob(numberOfPages, docJob, true, "success");

        } catch (Exception e) {
            //失败处理
            log.error("docjob {} failed deal to {}", docJob, e);
            try {
                FileUtils.deleteDirectory(tmpWorkDir);
                reportDocJob(0, docJob, false, e.getMessage());
            } catch (IOException e1) {
                log.error("failed: docjob  {}  deal to:{}", docJob, e1.getMessage());
                //e1.printStackTrace();
            }
        }

    }
    //报告job成功与否
    private void reportDocJob(int numberOfPages, DocJob docJob, boolean success, String message) throws IOException {
        DocJobResponse docJobResponse = new DocJobResponse();
        docJobResponse.setDocJobId(docJob.getId());
        docJobResponse.setSuccess(success);
        docJobResponse.setMessage(message);
        docJobResponse.setFinishTime(System.currentTimeMillis());
        docJobResponse.setNumOfPage(numberOfPages);
        if (!success) {
            docJobResponse.setRetryTime(1);
        }
        DocJobCallback jobCallback = RPC.getProxy(DocJobCallback.class, DocJobCallback.versionID, new InetSocketAddress("localhost", 8877), new Configuration());
        log.info("report job:{} to web : {}", docJob, docJobResponse);
        jobCallback.reportDocJob(docJobResponse);
    }

    private void convertToHtml(String fileName, File tmpWorkDir) throws IOException {
        String command = "soffice --headless --invisible --convert-to html " + fileName;
        Process process = Runtime.getRuntime().exec(command, null, tmpWorkDir);
        log.info("convert to html stdout:{}",IOUtils.toString(process.getInputStream()));
        log.info("convert to html stderr:{}",IOUtils.toString(process.getErrorStream()));
    }
    private void convertToPdf(String fileName, File tmpWorkDir) throws IOException {
        String command = "soffice --headless --invisible --convert-to pdf " + fileName;
        Process process = Runtime.getRuntime().exec(command, null, tmpWorkDir);
        log.info("convert to pdf stdout:{}",IOUtils.toString(process.getInputStream()));
        log.info("convert to pdf stderr:{}",IOUtils.toString(process.getErrorStream()));
    }
}
