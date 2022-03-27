package com.springbatch.excel.tutorial.domain;

import lombok.Data;

import java.nio.file.Path;

@Data
public class DataJsonOut {
    String fileNameIn;
    Path pathZip;
    DataJson in;
}
