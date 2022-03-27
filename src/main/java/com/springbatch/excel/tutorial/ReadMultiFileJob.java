package com.springbatch.excel.tutorial;

import com.springbatch.excel.tutorial.batch.JsonWriter;
import com.springbatch.excel.tutorial.batch.processors.DocumentProcessor;
import com.springbatch.excel.tutorial.domain.DataJson;
import com.springbatch.excel.tutorial.domain.DataJsonOut;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class ReadMultiFileJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value("file:c://files//trade*.json")
    private Resource[] resources;

    @Bean(name = "readFiles")
    public Job readFiles() {
        return jobBuilderFactory.get("readFiles").incrementer(new RunIdIncrementer()).
                flow(step1()).end().build();
    }

    @Bean
    public ItemWriter<? super DataJsonOut> consolJsonWrite(){
        return  new JsonWriter<>();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<DataJson, DataJsonOut>chunk(10)
                .reader(multiResourceItemReader())
                .processor(processor())
                .writer(consolJsonWrite()).build();
    }


    @Bean
    public DocumentProcessor processor(){
        return new DocumentProcessor();
    }


    @Bean
    public MultiResourceItemReader<DataJson> multiResourceItemReader() {
        MultiResourceItemReader<DataJson> resourceItemReader = new MultiResourceItemReader<DataJson>();
        resourceItemReader.setResources(resources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }

    @Bean
    public JsonItemReader<DataJson> reader() {
        JsonItemReader<DataJson> delegate = new JsonItemReaderBuilder<DataJson>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(DataJson.class))
                .name("documentItemReader")
                .build();
        return delegate;
    }
}