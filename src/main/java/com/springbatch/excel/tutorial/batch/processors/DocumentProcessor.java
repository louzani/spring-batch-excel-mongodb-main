package com.springbatch.excel.tutorial.batch.processors;

import com.springbatch.excel.tutorial.domain.DataJson;
import com.springbatch.excel.tutorial.domain.DataJsonOut;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.Objects;

public class DocumentProcessor implements ItemProcessor<DataJson, DataJsonOut> {

    @Value("classpath:files")
    private Path path;

    @Override
    public DataJsonOut process(DataJson dataJson) throws Exception {
        DataJsonOut out = new DataJsonOut();
        // check dataJson not empty
        if(Objects.nonNull(dataJson)) {
            out.setIn(dataJson);
            out.setFileNameIn(dataJson.getId());

            // if path.resolve(dataJson.getId()+".zip") not exist throws skippedException
            out.setPathZip(path.resolve(dataJson.getId() + ".zip"));
            return out;
        }

        return null;
    }
}
