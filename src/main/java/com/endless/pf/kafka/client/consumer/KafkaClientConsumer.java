package com.endless.pf.kafka.client.consumer;


import com.endless.pf.kafka.client.commonui.KafkaClientUiBase;
import com.endless.pf.kafka.client.ShowResult;
import com.alibaba.fastjson.JSONObject;

import com.endless.pf.kafka.client.partition.PartitionInfoComp;
import com.endless.tools.swt.base.SwtVoid;
import com.endless.tools.swt.base.YtComposite;
import com.endless.tools.swt.ui.text.YtText;
import com.endless.tools.swt.util.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaClientConsumer extends KafkaClientUiBase implements ShowResult {


    private PartitionInfoComp partitionInfoComp;
    private Button isPartitionBtn;
    private Button alwaysPollBtn;
    private Button descBtn;

    private ConsumerTask consumerTask;
    private YtComposite exComp;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        SwtVoid.createSwt(shell->{
            new KafkaClientConsumer(shell,0);
        });
    }

    public KafkaClientConsumer(Composite parent, int style) {
        super(parent, style);
    }


    public void initSend(YtComposite composite) {


        YtComposite sendComp = new YtComposite(composite, SWT.BORDER);
        sendComp.setGd(true,false);

        sendComp.createLabel("topic:");

        Text topicText = new Text(sendComp,SWT.BORDER);
        topicText.setLayoutData(LayoutUtil.createFillGridNoVer(1));
        topicText.setText("taxing");

        alwaysPollBtn = new Button(sendComp,SWT.CHECK);
        alwaysPollBtn.setText("一直获取");


        descBtn = new Button(sendComp,SWT.CHECK);
        descBtn.setText("输出内容详情");


        final Button sendBtn = new Button(sendComp,SWT.PUSH);
        sendBtn.setText("拉取");
        sendBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent eee) {
                if(consumerTask != null){
                    consumerTask.setStopPoll(true);
                }

                sendBtn.setSelection(false);

                ConsumerTaskVo  vo = new ConsumerTaskVo();
                vo.setLoopPoll(alwaysPollBtn.getSelection());
                vo.setShowDesc(descBtn.getSelection());
                vo.setTopic(topicText.getText());
                vo.setUsePartition(isPartitionBtn.getSelection());
                if(partitionInfoComp !=null) {
                    vo.setPartitionInfoList(partitionInfoComp.output());
                }

                if(consumerTask != null && !consumerTask.getStopPoll()){
                    consumerTask.setStopPoll(true);
                }

                try{

                    consumerTask = new ConsumerTask(KafkaClientConsumer.this);
                    consumerTask.start(vo, getJsonByItemList());

                }catch (Exception e) {
                    e.printStackTrace();
                    addResult(e.getLocalizedMessage());
                }

            }
        });

        Button stopBtn = new Button(sendComp,SWT.PUSH);
        stopBtn.setText("停止");
        stopBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sendBtn.setSelection(true);
                consumerTask.setStopPoll(true);
            }
        });
        Button commitBtn = new Button(sendComp,SWT.PUSH);
        commitBtn.setText("提交");
        commitBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(consumerTask != null) {
                    consumerTask.submit();
                }else{
                    addResult("consumer没有初始化，提交个啥子");
                }

            }
        });

        sendComp.setGridLayout(sendComp.getChildNum(),false);


        YtComposite partitionComp = new YtComposite(sendComp,0);
        partitionComp.setGridLayout(2,true);
        partitionComp.setGd(true,false);
        partitionComp.setLayoutData(LayoutUtil.createFillGridNoFill(4));
        Button partitionBtn = new Button(partitionComp,SWT.CHECK);
        partitionBtn.setText("设置partition");
        partitionBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            isPartitionBtn.setSelection(partitionBtn.getSelection());
            if(partitionBtn.getSelection()){
                partitionInfoComp.setData(getJsonByItemList(),topicText.getText());
                LayoutUtil.setExclude(partitionInfoComp,true);
            }else{
                LayoutUtil.setExclude(partitionInfoComp,false);
            }
            partitionInfoComp.getParent().getParent().layout();
            partitionInfoComp.getParent().getParent().getParent().layout();
            layout();

            }
        });

        isPartitionBtn = new Button(partitionComp,SWT.CHECK);
        isPartitionBtn.setText("启用partition选择");


        partitionInfoComp = new PartitionInfoComp(composite,SWT.BORDER);
        GridData gridData = LayoutUtil.createFillGridNoVer(1);
        gridData.heightHint = 150;
        partitionInfoComp.setLayoutData(gridData);


        LayoutUtil.setExclude(partitionInfoComp,false);
    }



    @Override
    public String getSavePath(){
        String path =  SwtVoid.getUserDir()+"kafka_client"+"/consumer_setting.json";
        return path;
    }




    protected JSONObject createProps(){
        JSONObject props = new JSONObject();
        //设置kafka集群的地址
        //生产环境：192.168.48.186:9092,192.168.48.187:9092,192.168.48.188:9092
        //测试环境

        props.put("bootstrap.servers", "192.168.240.42:9092,192.168.240.43:9092,192.168.240.44:9092");

        //设置消费者组，组名字自定义，组名字相同的消费者在一个组
        props.put("group.id", "my_group");
        //开启offset自动提交
        props.put("enable.auto.commit", "false");
        /// auto.offset.reset 使用默认配置 latest 还有：earliest
        props.put("auto.offset.reset", "latest");
        //序列化器
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");


        return props;
    }


    @Override
    protected void createConnection(JSONObject jsonObject) {
//        consumer = new KafkaConsumer<>(jsonObject);
//        consumerTask.getConsumer();
    }

    @Override
    protected boolean closeConnection() {
        consumerTask.getConsumer().close();
        return true;
    }
}
