package com.github.hbq969.demo.dao.key;

import com.github.hbq969.code.common.datasource.context.AbstractLookupKeyPolicy;
import com.github.hbq969.code.common.spring.context.SpringContext;

public class MariadbKey extends AbstractLookupKeyPolicy {
    @Override
    protected String getDatasourceKey(SpringContext context) {
        return "mariadb";
    }
}
