/*
package com.springbatch.excel.tutorial.batch;

import com.springbatch.excel.tutorial.batch.listeners.JobCompletionListener;
import com.springbatch.excel.tutorial.batch.processors.EmployeeItemProcessor;
import com.springbatch.excel.tutorial.batch.validators.EmployeeJobParametersValidator;
import com.springbatch.excel.tutorial.domain.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

*/
/**
 * Configuration for batch
 *//*

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    public final JobBuilderFactory jobBuilderFactory;

    public final StepBuilderFactory stepBuilderFactory;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public JobParametersValidator jobParametersValidator() {
        return new EmployeeJobParametersValidator();
    }

    @Bean
    public JobParametersValidator compositeJobParametersValidator() {
        CompositeJobParametersValidator bean = new CompositeJobParametersValidator();
        bean.setValidators(Collections.singletonList(jobParametersValidator()));
        return bean;
    }

  */
/*  @Bean
    public ItemProcessor<Employee, Employee> itemProcessor() {
        return new EmployeeItemProcessor();
    }

    @Bean
    public ItemReader<Employee> itemReader() {
        return new EmployeeItemReader();
    }
*//*

   */
/* @Bean
    public MongoItemWriter<Employee> writer(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<Employee>().template(mongoTemplate).collection("employee")
                .build();
    }*//*


    @Bean
    public  RecordWriter<Employee> consolWrite(){
        return  new RecordWriter<>();
    }


    */
/**
     * step declaration
     * @return {@link Step}
     *//*

  */
/*  @Bean
    public Step employeeStep(MongoItemWriter<Employee> itemWriter) {
        return stepBuilderFactory.get("employeeStep")
                .<Employee, Employee>chunk(50)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter)
                .build();
    }*//*


    @Bean
    public Step readfromDB(MongoItemReader<Employee> itemReader) {
        return stepBuilderFactory.get("employeeStep")
                .<Employee, Employee>chunk(1)
                .reader(itemReader)
        //        .processor(itemProcessor())
                .writer(consolWrite())
                .build();
    }



    */
/**
     * job declaration
     * @param listener {@link JobCompletionListener}
     * @return {@link Job}
     *//*

    @Bean
    public Job employeeJob(JobCompletionListener listener, Step readfromDB) {
        return jobBuilderFactory.get("employeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(readfromDB)
                .end()
                .validator(compositeJobParametersValidator())
                .build();
    }


    @Bean
    public MongoItemReader<Employee> reader(MongoTemplate mongoTemplate) {
        MongoItemReader<Employee> reader = new MongoItemReader<>();
        reader.setTemplate(mongoTemplate);
        reader.setSort(new HashMap<String, Sort.Direction>() {{
            put("_id", Sort.Direction.DESC);
        }});
        reader.setTargetType(Employee.class);
        reader.setQuery("{department : 'Actuary' }");
        return reader;
    }


}
*/
