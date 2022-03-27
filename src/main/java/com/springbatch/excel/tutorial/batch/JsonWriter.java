package com.springbatch.excel.tutorial.batch;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class JsonWriter<DataJsonOut> implements ItemWriter<com.springbatch.excel.tutorial.domain.DataJsonOut> {



    @Override
    public void write(List<? extends com.springbatch.excel.tutorial.domain.DataJsonOut> list) throws Exception {
        list.forEach(t -> System.out.println("------------" + t.toString()));

    }
}
