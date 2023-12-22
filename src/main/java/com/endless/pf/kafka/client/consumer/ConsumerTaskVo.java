package com.endless.pf.kafka.client.consumer;

import com.endless.pf.kafka.client.partition.PartitionInfoVo;

import java.util.List;

public class ConsumerTaskVo {

    private String topicText;
    private boolean alwaysPollBtn;
    private boolean showDescBtn;
    private List<PartitionInfoVo> partitionInfoVoList;
    private boolean usePartitionBtn = false;


    public String getTopicText() {
        return topicText;
    }

    public void setTopicText(String topicText) {
        this.topicText = topicText;
    }


    public boolean isAlwaysPollBtn() {
        return alwaysPollBtn;
    }

    public void setAlwaysPollBtn(boolean alwaysPollBtn) {
        this.alwaysPollBtn = alwaysPollBtn;
    }

    public boolean isShowDescBtn() {
        return showDescBtn;
    }

    public void setShowDescBtn(boolean showDescBtn) {
        this.showDescBtn = showDescBtn;
    }

    public List<PartitionInfoVo> getPartitionInfoList() {
        return partitionInfoVoList;
    }

    public void setPartitionInfoList(List<PartitionInfoVo> partitionInfoVoList) {
        this.partitionInfoVoList = partitionInfoVoList;

    }

    public boolean isUsePartitionBtn() {
        return usePartitionBtn;
    }

    public void setUsePartitionBtn(boolean usePartitionBtn) {
        this.usePartitionBtn = usePartitionBtn;
    }

    @Override
    public String toString() {
        return "ConsumerTaskVo{" +
                "topic='" + topicText + '\'' +
                ", loopPoll=" + alwaysPollBtn +
                ", showDesc=" + showDescBtn +
                ", topicList=" + partitionInfoVoList +
                '}';
    }
}
