package com.github.hbq969.demo.dao;

import com.github.hbq969.code.common.datasource.DS;
import com.github.hbq969.demo.dao.key.MariadbKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS(MariadbKey.class)
public interface RagRepository {
    void clearVectorStore();
}
