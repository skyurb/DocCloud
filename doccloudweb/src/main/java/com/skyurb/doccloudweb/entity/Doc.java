package com.skyurb.doccloudweb.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="doc")
@Data
public class Doc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自动生成id
    private int id;
    @Column(name = "md5")//如果与数据库一致，则不用加
    private String md5;
    @Column(name = "doc_name")
    private String docName;
    @Column(name = "doc_type")
    private String docType;
    @Column(name = "doc_status")
    private String docStatus;
    @Column(name = "doc_size")
    private String docSize;
    @Column(name = "doc_dir")
    private String docDir;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "doc_create_time")
    private Date docCreateTime;
    @Column(name = "doc_comment")
    private String docComment;
    @Column(name = "doc_permission")
    private String docPermission;

}
