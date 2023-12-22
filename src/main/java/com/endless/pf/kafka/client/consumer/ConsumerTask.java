package com.endless.pf.kafka.client.consumer;

import cn.hutool.core.io.FileUtil;
import com.endless.pf.kafka.client.ShowResult;
import com.endless.pf.kafka.client.commonui.KafkaClientUiBase;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.endless.pf.kafka.client.partition.PartitionInfoVo;
import com.endless.tools.swt.base.SwtVoid;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsumerTask extends Thread {

    private ConsumerTaskVo taskVo ;
    private ShowResult showResult;
    private volatile Consumer<String,String> consumer;
    private JSONObject connectionParam;
    private boolean stopPoll = false;
    private String clientId;

    public ConsumerTask(ShowResult showResult ){
        this.showResult = showResult;
    }

    public void start(ConsumerTaskVo vo , JSONObject connectionParam){


        this.taskVo = vo;
        this.connectionParam = connectionParam;

        boolean b = KafkaClientUiBase.checkAndCancel(connectionParam);
        if(b){
            addResult("已经取消操作");
            return ;
        }
        initConnection();

        try{
            //是否不需要设置分区
            if(taskVo.isUsePartitionBtn() ){
                List<TopicPartition> list = new ArrayList<>();
                List<PartitionInfoVo> topicList = taskVo.getPartitionInfoList();
                if(topicList.isEmpty()){
                    addResult("请选择分区");
                    return ;
                }
                topicList.forEach(topicInfo -> {
                    TopicPartition partition0 = new TopicPartition(taskVo.getTopicText(), topicInfo.getPartitionNo());
                    list.add(partition0);
                });
                consumer.assign(list);

                topicList.forEach(topicInfo -> {
                    TopicPartition partition0 = new TopicPartition(taskVo.getTopicText(), topicInfo.getPartitionNo());
                    //暂时看来只会对单次的consumer起作用，可以在此次指定到特定的offset
                    //但是，每次使用完，lastOffset都会跑到最后的offset中。也就是说。
                    //使用前：
                    //TopicInfo{partitionNo=0, offset=0, allSize=84, lag=0, change=false, topic='taxing'}
                    //然后虽然我们只指定了某个partition的offset，但是使用之后
                    //TopicInfo{partitionNo=0, offset=84, allSize=84, lag=0, change=false, topic='taxing'}
                    //所有的partition的offset都会自动偏移到最后。
                    consumer.seek(partition0, topicInfo.getOffset());
//                    consumer.seekToBeginning(Arrays.asList(partition0));
                });

            }else{
                //正常订阅
                consumer.subscribe(Arrays.asList(taskVo.getTopicText()));
            }
        }catch (Exception e){
            addResult(e.getLocalizedMessage());
        }
        clientId = (String)ReflectUtil.getFieldValue(consumer, "clientId");
        addResult("存储路径是：   "+ SwtVoid.getUserDir()+"kafka.txt");
        addResult("consumer初始化成功，正在拉取，如果没有数据，将休眠5秒");
        start();


    }

    @Override
    public void run(){
        stopPoll = false;
        //表示是否已经连接拉取到了一个
        boolean flag = false;
        while (true){
            if(stopPoll){
                break ;
            }
            //poll在刚开始的时候，会一直拉取到空的数据内容。要一段时间之后才有数据，具体为什么，不清楚，但不是代码问题
            ConsumerRecords<String, String> records = consumer.poll(5000);
            if(records.isEmpty()){
                addResult("没有拉取到数据，将休眠5秒");
            }

            for (ConsumerRecord<String, String> record : records) {
                flag = true;
                if(taskVo.isShowDescBtn()){
                    String format = String.format("offset = %d, key = %s, partition = %s", record.offset(), record.key(), record.partition());
                    String format2 = String.format("offset = %d, key = %s, value = %s, partition = %s", record.offset(), record.key(), record.value(), record.partition());
                    FileUtil.writeString(format2,SwtVoid.getUserDir()+taskVo.getTopicText()+"-kafka.txt","UTF-8");
                    addResult(format2);
//                    System.out.printf("offset = %d, key = %s,  partition = %s%n", record.offset(), record.key(), record.partition());
                }else{
                    String format = String.format("offset = %d, key = %s,  partition = %s", record.offset(), record.key(), record.partition());
                    FileUtil.writeString(format,SwtVoid.getUserDir()+taskVo.getTopicText()+"-kafka.txt","UTF-8");
                    addResult(format);
//                    System.out.printf("offset = %d, key = %s,  partition = %s%n", record.offset(), record.key(),  record.partition());
                }

            }

            if(!taskVo.isAlwaysPollBtn() && flag){
//            if(!taskVo.isLoopPoll()){
                stopPoll = true;
                addResult("停止拉取，仅获取一次");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
//        destroyConnection();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer.commitAsync();
        addResult("已停止拉取");
    }

    public void addResult(String content){
        showResult.addResult("线程： "+getId()+" clientId： "+clientId+" -> "+content);
    }


    private void initConnection(){
        consumer = new KafkaConsumer<>(connectionParam);

    }

    private void destroyConnection(){
        if(consumer != null) {
            consumer.close();
            consumer = null;
            addResult("consumer已销毁");
        }
    }

    public void submit(){
        stopPoll = true;
        consumer.commitAsync();
        addResult("提交完成");
    }


    public Consumer getConsumer(){
        return consumer;
    }

    public ConsumerTask setStopPoll(boolean stopPoll){
    	if(this.stopPoll){
    		addResult("consumer已经是停止状态");
    	}
        this.stopPoll = stopPoll;
        	

        return this;
    }


    public boolean getStopPoll(){
        return stopPoll;
    }





}
