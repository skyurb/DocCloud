package com.skyurb.doccloud.util;

import com.skyurb.doccloud.job.DocIndex;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

/*
*@ClassName:FullTextIndexUtil
 @Description:TODO
 @Author:
 @Date:2018/10/31 15:21 
 @Version:v1.0
*/
public class FullTextIndexUtil {
    static SolrClient solrclient;
    static final String SOLR_URL = "http://192.168.203.74:8983/solr/doccloud";

    static {
        solrclient = new HttpSolrClient(SOLR_URL);
    }

    public static void add(DocIndex docIndex) throws IOException, SolrServerException {
        solrclient.addBean(docIndex);
        solrclient.commit();
    }

    public static void main(String[] args) throws IOException, SolrServerException {
//        DocIndex docIndex = new DocIndex();
//        String content = PdfUtil.getContent("F:\\tmp\\docjobdaemon\\6be98a22-694f-4774-901d-fbef88c32770\\毒笑小说.pdf");
//        docIndex.setDocContent(content);
//        docIndex.setId("3");
//        docIndex.setDocName("毒笑小说");
//        docIndex.setUrl("hhh");
//        docIndex.setDocType("txt");
//        add(docIndex);
        //SolrQuery params = new SolrQuery();

//        System.out.println("======================query===================");
//
//        params.set("q", "docContent:中国* or id:bigdata");
//        params.set("start", 0);
//        params.set("rows", 20);
//        params.set("sort", "id asc");
//
//        SolrDocumentList docs = query(params);
//        for (SolrDocument doc : docs) {
//            // 多值查询
//            @SuppressWarnings("unchecked")
//
//            String id = (String) doc.getFieldValue("id");
//            String docName = (String) doc.getFieldValue("docName");
//            String docContent = String.valueOf(doc.getFieldValue("docContent"));
//            String docType = (String) doc.getFieldValue("docType");
//
//            System.out.println("id:"+id+"\t name:" + docName + "\t description:"+docContent+"\t price:"+docType );
   // }

    }

    public static SolrDocumentList query(SolrQuery params) {


        try {
            QueryResponse rsp = solrclient.query(params);
            SolrDocumentList docs = rsp.getResults();
            System.out.println("查询内容:" + params);
            System.out.println("文档数量：" + docs.getNumFound());
            System.out.println("查询花费时间:" + rsp.getQTime());

            System.out.println("------query data:------");
            return docs;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
