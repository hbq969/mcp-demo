package com.github.hbq969.demo.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ImportModel {
    private MultipartFile[] files;
    private String defaultSystem;
}
