package com.endless.pf.kafka.client.partition;

public class PartitionInfoVo implements Comparable<PartitionInfoVo>{

    private int partitionNo;
    private long offset;
    private long allSize;
    private long lag;
    /**
     * 判断是否被用户修改过偏移量
     * */
    private boolean change;
    private String topic;

    public int getPartitionNo() {
        return partitionNo;
    }

    public void setPartitionNo(int partitionNo) {
        this.partitionNo = partitionNo;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getAllSize() {
        return allSize;
    }

    public void setAllSize(long allSize) {
        this.allSize = allSize;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public int compareTo(PartitionInfoVo o) {
        return partitionNo-o.partitionNo;
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "partitionNo=" + partitionNo +
                ", offset=" + offset +
                ", allSize=" + allSize +
                ", lag=" + lag +
                ", change=" + change +
                ", topic='" + topic + '\'' +
                '}';
    }

    public long getLag() {
        return lag;
    }

    public void setLag(long lag) {
        this.lag = lag;
    }

    public boolean getChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

}
