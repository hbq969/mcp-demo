package com.github.hbq969.demo.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class ReaderModel {
    private String url;
    private String protocol;

    public String getFullPath() {
        if (StrUtil.isEmpty(url) || StrUtil.isEmpty(protocol))
            throw new IllegalArgumentException();
        return protocol + "//" + url;
    }
}
