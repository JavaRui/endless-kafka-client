package com.endless.pf.kafka.client.producer;

import cn.hutool.core.collection.LineIter;
import com.endless.pf.kafka.client.ShowResult;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * 生产消息的task
 * */
public class ProducerTask implements Runnable{

    /**
     * 发送次数
     * */
    private int num;
    /**
     * 消息内容
     * */
    private String content;
    /**
     * 消息发送者
     * */
    private Producer producer;
    /**
     * 消息发送的topic
     * */
    private String topic;
    ShowResult showResult;

    public ProducerTask(ShowResult showResult , Producer producer, int num, String topic, String content) {
        this.num = num;
        this.content = content;
        this.producer = producer;
        this.topic = topic;
        this.showResult = showResult;
    }

    @Override
    public void run() {
        for( int i = 0 ; i < num ; i ++ ){

            String  realContent = KafkaClientProducer.getValueByExpression(content);
            producer.send(new ProducerRecord<String, String>(topic, realContent), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e12) {

                    if(e12 != null){
                        showResult.addResult(e12.getLocalizedMessage());
                        return ;
                    }

                    try{
                        String result = "topic:  "+recordMetadata.topic()+"  "+" partition:  "+recordMetadata.partition()+"  offset:  "+recordMetadata.offset();
                        showResult.addResult(result);
                    }catch (Exception e1){
                        showResult.addResult("---:   "+e1.getLocalizedMessage());

                    }
                }
            });
        }

        producer.close();
        producer = null;
    }
}
