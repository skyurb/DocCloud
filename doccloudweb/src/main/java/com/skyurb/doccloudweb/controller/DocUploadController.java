package com.skyurb.doccloudweb.controller;

import com.skyurb.doccloudweb.dao.DocRepository;
import com.skyurb.doccloudweb.entity.Doc;
import com.skyurb.doccloudweb.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Controller
@RequestMapping("/doc")
public class DocUploadController {
    @Autowired
    private DocRepository docRepository;
    public static final String[] DOC_SUFFIXS = new String[]{"doc", "docx", "ppt", "pptx", "txt", "xls", "xlsx", "pdf"};
    public static final int DOC_MAX_SIZE = 128 * 1024 * 1024;

    @RequestMapping("/list")
    @ResponseBody
    public Doc listDoc(){
        return  docRepository.findById(1).get();

    }

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

            Path path = Paths.get("F:\\test\\" + file.getOriginalFilename());
            Files.write(path,bytes);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "upload success";
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
