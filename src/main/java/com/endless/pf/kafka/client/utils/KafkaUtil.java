package com.endless.pf.kafka.client.utils;

import com.alibaba.fastjson.JSONObject;
import com.endless.pf.kafka.client.partition.PartitionInfoVo;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class KafkaUtil {
    private static Logger logger = LoggerFactory.getLogger(KafkaUtil.class);

    /**
     * 创建两个consumer去
     * 获取topic的信息，如偏移量，总量，分区id。
     * 然后再合并为一个
     * @param jsonObject
     * @param topic
     * @return
     */
    public static List<PartitionInfoVo> getTopicInfo(JSONObject jsonObject, String topic){

        /// auto.offset.reset 使用默认配置 latest
//        props.put("auto.offset.reset", "earliest");

        jsonObject.put("auto.offset.reset","earliest");
        KafkaConsumer earliestConsumer = new KafkaConsumer<>(jsonObject);
        List<PartitionInfoVo> ePartitionInfoVo = getTopicInfo(earliestConsumer, topic,false);
        earliestConsumer.close();

        //专门用来获取latest的数量
        jsonObject.put("auto.offset.reset","latest");
        jsonObject.put("group.id", topic+"_latest_group");
        KafkaConsumer latestConsumer = new KafkaConsumer<>(jsonObject);
        List<PartitionInfoVo> lPartitionInfoVo = getTopicInfo(latestConsumer, topic,true);
        latestConsumer.close();

        for(int i = 0; i < ePartitionInfoVo.size() ; i ++ ){
            PartitionInfoVo eInfo = ePartitionInfoVo.get(i);
            PartitionInfoVo partitionInfoVo = lPartitionInfoVo.get(i);
            eInfo.setAllSize(partitionInfoVo.getAllSize());


        }

        return ePartitionInfoVo;
    }

    /**
     * 获取topic的信息，如偏移量，总量，分区id。
     * @param consumer
     * @param topic
     * @param last 是否获取最后一个可用的偏移量，也就是总量
     * @return
     */
    public static List<PartitionInfoVo> getTopicInfo(Consumer<Object, Object> consumer , String topic , boolean last){
        List<PartitionInfoVo> partitionInfoVoList = new LinkedList<>();
        List<org.apache.kafka.common.PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        List<TopicPartition> tp =new ArrayList<>();
        partitionInfos.forEach(partitionInfo -> {
            tp.add(new TopicPartition(topic,partitionInfo.partition()));

        });

        if(last){
            //偏移到最后
            consumer.assign(tp);
            consumer.seekToEnd(tp);
        }else{
            consumer.assign(tp);
        }


        partitionInfos.forEach(partitionInfo -> {
            long position = consumer.position(new TopicPartition(topic, partitionInfo.partition()));
            logger.info("Partition " + partitionInfo.partition() + " 's latest offset is '" +  position);
            //            consumer.seekToEnd(tp);
//            System.out.println("end   Partition " + partitionInfo.partition() + " 's latest offset is '" +
//                    consumer.position(new TopicPartition(topic, partitionInfo.partition())));
            PartitionInfoVo info = new PartitionInfoVo();
            info.setTopic(topic);
            info.setPartitionNo(partitionInfo.partition());
            info.setAllSize(consumer.position(new TopicPartition(topic, partitionInfo.partition())));
            info.setOffset(consumer.position(new TopicPartition(topic, partitionInfo.partition())));
            partitionInfoVoList.add(info);
        });

        Collections.sort(partitionInfoVoList); // 按年龄排序
        return partitionInfoVoList;
    }





}
