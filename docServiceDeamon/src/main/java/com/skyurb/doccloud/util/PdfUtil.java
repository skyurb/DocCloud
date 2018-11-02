package com.skyurb.doccloud.util;

import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/*
*@ClassName:PdfUtil
 @Description:TODO
 @Author:
 @Date:2018/10/30 12:15 
 @Version:v1.0
*/
public class PdfUtil {


//获取页数
    public static int getNumberOfPages(String filePath) throws IOException {
        PDDocument pdfReader = PDDocument.load(new File(filePath));
        int pages = pdfReader.getNumberOfPages();
//        if (pdfReader!=null){
//        pdfReader.close();
//        }
        pdfReader.close();
        return pages;
    }
    //提取pdf中的文本
    public static String getContent(String filePath) throws IOException {
        PDFParser pdfParser = new PDFParser(new RandomAccessFile(new File(filePath), "rw"));
        pdfParser.parse();
        PDDocument pdDocument = pdfParser.getPDDocument();
        String text = new PDFTextStripper().getText(pdDocument);
        pdDocument.close();


        return text;
    }

    public static void getThumbnails(String filePath,String outPath) throws IOException {
        // 利用PdfBox生成图像
        PDDocument pdDocument = PDDocument.load(new File(filePath));
        PDFRenderer renderer = new PDFRenderer(pdDocument);
    // 构造图片
        BufferedImage img_temp = renderer.renderImageWithDPI(0, 30, ImageType.RGB);
    // 设置图片格式
        Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix("png");
    // 将文件写出
        ImageWriter writer =  it.next();
        ImageOutputStream imageout = ImageIO.createImageOutputStream(new FileOutputStream(outPath));
        writer.setOutput(imageout);
        writer.write(new IIOImage(img_temp, null, null));
        img_temp.flush();
        imageout.flush();
        imageout.close();
        pdDocument.close();
    }



    public static void main(String[] args) throws IOException {

        //int numberOfPages = getNumberOfPages("d:\\hadoopclientcode.pdf");
        //System.out.println(numberOfPages);

        //getThumbnails("d:\\hadoopclientcode.pdf","d:\\test.png");
        String content = getContent("F:\\tmp\\docjobdaemon\\6be98a22-694f-4774-901d-fbef88c32770\\毒笑小说.pdf");
        System.out.println(content);
    }
}
