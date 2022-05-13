package com.springbatch.excel.tutorial.partitioner;

import com.springbatch.excel.tutorial.batch.MultiFilesJobLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JsonPartitioner implements Partitioner{

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPartitioner.class);

    @Value("file:c://files//trade*.json")
    private String locationResource;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionMap = new HashMap<String, ExecutionContext>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(locationResource);

           int d = resources.length / gridSize;


            ExecutionContext context = new ExecutionContext();
            partitionMap.put(String.valueOf(1), context);
            context.putInt("fromId", 0);
            context.putInt("toId", 2);

           ExecutionContext context2 = new ExecutionContext();
            partitionMap.put(String.valueOf(2), context2);
            context2.putInt("fromId", 3);
            context2.putInt("toId", 5);

        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("END : Created Partitions of size: "+partitionMap.size());
        return partitionMap;
    }
}
