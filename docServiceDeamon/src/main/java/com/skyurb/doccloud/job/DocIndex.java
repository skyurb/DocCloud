package com.skyurb.doccloud.job;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
@Data
public class DocIndex {
    @Field
    private String id;
    @Field
    private String docName;
    @Field
    private String url;
    @Field
    private String docContent;
    @Field
    private String docType;


}
