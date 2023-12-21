package com.endless.pf.kafka.client.consumer;

import com.endless.pf.kafka.client.partition.PartitionInfoVo;

import java.util.List;

public class ConsumerTaskVo {

    private String topic ;
    private boolean loopPoll;
    private boolean showDesc;
    private List<PartitionInfoVo> partitionInfoVoList;
    private boolean usePartition = false;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public boolean isLoopPoll() {
        return loopPoll;
    }

    public void setLoopPoll(boolean loopPoll) {
        this.loopPoll = loopPoll;
    }

    public boolean isShowDesc() {
        return showDesc;
    }

    public void setShowDesc(boolean showDesc) {
        this.showDesc = showDesc;
    }

    public List<PartitionInfoVo> getPartitionInfoList() {
        return partitionInfoVoList;
    }

    public void setPartitionInfoList(List<PartitionInfoVo> partitionInfoVoList) {
        this.partitionInfoVoList = partitionInfoVoList;

    }

    public boolean isUsePartition() {
        return usePartition;
    }

    public void setUsePartition(boolean usePartition) {
        this.usePartition = usePartition;
    }

    @Override
    public String toString() {
        return "ConsumerTaskVo{" +
                "topic='" + topic + '\'' +
                ", loopPoll=" + loopPoll +
                ", showDesc=" + showDesc +
                ", topicList=" + partitionInfoVoList +
                '}';
    }
}
