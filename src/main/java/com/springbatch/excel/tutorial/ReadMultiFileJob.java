.package com.springbatch.excel.tutorial;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Configuration
@EnableBatchProcessing
public class ReadMultiFileJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value("file:c://files//trade*.json")
    private Resource[] resources;

    @Value("file:c://files//trade*.json")
    private String locationResource;

    @Bean(name = "readFiles")
    public Job readFiles() throws JsonProcessingException {
        return jobBuilderFactory.get("readFiles").incrementer(new RunIdIncrementer()).
                flow(partitionStep()).end().build();
    }

    @Bean
    public ItemWriter<? super DataJsonOut> consolJsonWrite(){
        return  new JsonWriter<>();
    }


    @Bean("partitionStep")
    public Step partitionStep() throws JsonProcessingException {
        return stepBuilderFactory.get("partitionStep")
                .partitioner("step1", partitioner())
                .partitionHandler(partitionHandler(stepBuilderFactory))
                .step(step1())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("partitionerStep")
    public MultiResourcePartitioner partitioner() {
        MultiResourcePartitioner partitioner
                = new MultiResourcePartitioner();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(locationResource);
            partitioner.setResources(resources);
            return partitioner;
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving"
                    + " the input file pattern.", e);
        }

    }

    @Bean("taskExecutorStep2")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(5);
        return simpleAsyncTaskExecutor;
    }

    @Bean("partitionHandlerstep2")
    public PartitionHandler partitionHandler(StepBuilderFactory stepBuilderFactory) throws JsonProcessingException {
        final TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(5);
        handler.setTaskExecutor(taskExecutor());
        handler.setStep(step1());
        return handler;
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

    public static void main(String[] args) {
        for (int i = 0; i <1000 ; i++) {
            try{
            String name = "c://files//trade"+i+".json";
            Files.copy(Paths.get("c://files//trade1.json"), Paths.get(name), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        }
    }
}
