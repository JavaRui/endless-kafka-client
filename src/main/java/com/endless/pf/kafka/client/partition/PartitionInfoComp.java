package com.endless.pf.kafka.client.partition;

import com.alibaba.fastjson.JSONObject;
import com.endless.pf.kafka.client.utils.KafkaUtil;
import com.endless.tools.swt.base.YtComposite;
import com.endless.tools.swt.sc.BaseScPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * partition设置面板
 */
public class PartitionInfoComp extends BaseScPart<PartitionInfoVo,List<PartitionInfoVo>> {
    Logger logger = LoggerFactory.getLogger(PartitionInfoComp.class);

    public PartitionInfoComp(Composite parent , int style) {
        super(parent,style);
    }

    public void setData(JSONObject consumerJson , String topic){
        List<PartitionInfoVo> partitionInfoVoList = KafkaUtil.getTopicInfo(consumerJson, topic);
        input(partitionInfoVoList);

    }


    /**
     * 获取被选择的partition
     * @return
     */
    public List<PartitionInfoVo> getPartitionList(){
        List<PartitionInfoVo> partitionInfoVoList = new LinkedList<>();
        getItemList().forEach(partitionInfoItem -> {
            if(((PartitionInfoItem)partitionInfoItem).isSelect()){

                partitionInfoVoList.add(((PartitionInfoItem)partitionInfoItem).output());
            }
        });
        return partitionInfoVoList;
    }


    @Override
    protected PartitionInfoItem createItem(YtComposite comp, PartitionInfoVo topicInfo) {
        PartitionInfoItem partitionInfoItem = new PartitionInfoItem(comp, SWT.BORDER);
        partitionInfoItem.input(topicInfo);
        return partitionInfoItem;
    }




//    @Override
//    public void input(PartitionInfo obj) {
//        super.input(obj);
//    }

    @Override
    public List<PartitionInfoVo> output() {
        return getPartitionList();
    }
}
