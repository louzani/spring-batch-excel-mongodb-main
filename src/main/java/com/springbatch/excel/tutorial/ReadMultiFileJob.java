package com.springbatch.excel.tutorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springbatch.excel.tutorial.batch.JsonWriter;
import com.springbatch.excel.tutorial.batch.processors.DocumentProcessor;
import com.springbatch.excel.tutorial.domain.DataJson;
import com.springbatch.excel.tutorial.domain.DataJsonOut;
import com.springbatch.excel.tutorial.partitioner.JsonPartitioner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
public class ReadMultiFileJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


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
                .partitioner("step1",partitioner())
                .partitionHandler(partitionHandler())
                .build();
    }

    @Bean("partitionerStep")

    public JsonPartitioner partitioner() {
        return new JsonPartitioner();
    }

/*
    @Bean("partitionerStep")
    public MultiResourcePartitioner partitioner() {
        MultiResourcePartitioner partitioner
                = new MultiResourcePartitioner();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(locationResource);
            partitioner.setResources(resources);
            partitioner.partition(10);
            return partitioner;
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving"
                    + " the input file pattern.", e);
        }

    }
*/

   @Bean("taskExecutorStep2")
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }



/*    @Bean("taskThExecutor")
    TaskExecutor taskThExecutor () {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(10);
        t.setMaxPoolSize(100);
        t.setQueueCapacity(50);
        t.setAllowCoreThreadTimeOut(true);
        t.setKeepAliveSeconds(120);
        return t;
    }*/

    @Bean("partitionHandlerstep2")
    public PartitionHandler partitionHandler() throws JsonProcessingException {
        final TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setTaskExecutor(taskExecutor());
        handler.setGridSize(3);
        handler.setStep(step1());

        return handler;
    }




    @Bean("step1")
    public Step step1() {
        return stepBuilderFactory.get("step1").<DataJson, DataJsonOut>chunk(10)
                .reader(multiResourceItemReader(null))
                .processor(processor())
                .writer(consolJsonWrite())
                .build();
    }


    @Bean
    public DocumentProcessor processor(){
        return new DocumentProcessor();
    }


    @Bean("multiResourceItemReader")
    @StepScope
    public MultiResourceItemReader<DataJson> multiResourceItemReader(@Value("#{stepExecutionContext['lot']}") Integer lot){

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = new Resource[0];
        try {
            resources = resolver.getResources("file:c://files//"+lot+"//trade*.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  new MultiResourceItemReaderBuilder<DataJson>()
                .delegate(reader())
                .name("itemReader")
                .resources(resources).build();
    }

     /*   @Bean
        @StepScope
        public FlatFileItemReader<DataJson> readerCsv() {
            FlatFileItemReader<DataJson> reader = new FlatFileItemReader<DataJson>();
            reader.setLineMapper(new DefaultLineMapper() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames(new String[]{"id", "name"});
                }});

                setFieldSetMapper(new BeanWrapperFieldSetMapper<DataJson>() {{
                    setTargetType(DataJson.class);
                }});
            }});
            return reader;
        }*/
/*
    @Bean
    public MultiResourceItemReader<DataJson> multiResourceItemReader() {


        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources ;
        MultiResourceItemReader<DataJson> resourceItemReader = new MultiResourceItemReader<DataJson>();

        try {
            resources = resolver.getResources(locationResource);
            resourceItemReader.setResources(resources);
            resourceItemReader.setDelegate(reader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resourceItemReader;
    }*/


    @Bean("reader")
    @StepScope
    public JsonItemReader<DataJson> reader() {
        JsonItemReader<DataJson> delegate = new JsonItemReaderBuilder<DataJson>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(DataJson.class))
                .name("documentItemReader")
                .strict(true)
                .build();
        return delegate;
    }


/*
    @Bean
    public SynchronizedItemStreamReader<DataJson> itemReader() {
        SynchronizedItemStreamReader<DataJson> synchronizedItemStreamReader = new SynchronizedItemStreamReader<>();
        JsonItemReader<DataJson> delegate = new JsonItemReaderBuilder<DataJson>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(DataJson.class))
                .name("documentItemReader")
                .build();
        synchronizedItemStreamReader.setDelegate(delegate);
        return synchronizedItemStreamReader;
    }*/

  /*  public static void main(String[] args) {
        for (int i = 0; i <1000 ; i++) {
            try{
            String name = "c://files//trade"+i+".json";
            Files.copy(Paths.get("c://files//trade1.json"), Paths.get(name), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        }
    }*/
}
