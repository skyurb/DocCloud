package com.skyurb.doccloudweb.dao;


import com.skyurb.doccloudweb.entity.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocRepository extends JpaRepository<Doc,Integer> {
    Optional<Doc> findByMd5(String md5);
}
