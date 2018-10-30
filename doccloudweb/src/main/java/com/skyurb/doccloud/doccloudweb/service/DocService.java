package com.skyurb.doccloud.doccloudweb.service;


import com.skyurb.doccloud.doccloudweb.dao.DocRepository;
import com.skyurb.doccloud.doccloudweb.entity.Doc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class DocService {

    @Autowired
    private DocRepository docRepository;

    public Optional<Doc> findById(int id){
        return  docRepository.findById(id);

    }
    public Optional<Doc> findByMd5(String md5){
        return  docRepository.findByMd5(md5);
    }
    public Doc save(Doc docEntity) {
        return docRepository.save(docEntity);
    }
}
