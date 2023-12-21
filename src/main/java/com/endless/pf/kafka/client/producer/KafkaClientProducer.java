package com.endless.pf.kafka.client.producer;


import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.RandomUtil;
import com.endless.pf.kafka.client.ShowResult;
import com.endless.pf.kafka.client.commonui.KafkaClientUiBase;
import com.endless.pf.kafka.client.commonui.ParamItem;
import com.alibaba.fastjson.JSONObject;
import com.endless.tools.swt.base.SwtVoid;
import com.endless.tools.swt.base.YtComposite;
import com.endless.tools.swt.mgr.MsgDlgMgr;
import com.endless.tools.swt.ui.text.YtText;
import com.endless.tools.swt.util.LayoutUtil;
import org.apache.kafka.clients.producer.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KafkaClientProducer extends KafkaClientUiBase implements ShowResult {

    private Producer<String, String> producer = null;

    ExecutorService executorService = Executors.newFixedThreadPool(5);





    public static void main(String[] args) {

//        JSONObject props = new JSONObject();        //设置kafka集群的地址
//        props.put("bootstrap.servers", "hadoop01:9092,hadoop02:9092,hadoop03:9092");
//        //ack模式，all是最慢但最安全的
//        props.put("acks", "-1");
//        //失败重试次数
//        props.put("retries", 0);
//        //每个分区未发送消息总字节大小（单位：字节），超过设置的值就会提交数据到服务端
//        props.put("batch.size", 10);
//        //props.put("max.request.size",10);
//        //消息在缓冲区保留的时间，超过设置的值就会被提交到服务端
//        props.put("linger.ms", 10000);
//        //整个Producer用到总内存的大小，如果缓冲区满了会提交数据到服务端
//        //buffer.memory要大于batch.size，否则会报申请内存不足的错误
//        props.put("buffer.memory", 10240);
//        //序列化器
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//
//        Producer<String, String> producer = new KafkaProducer<>(props);
////        JSONObject jsonObject = new JSONObject();
////        jsonObject.putAll(props.);
//        System.out.println(props);


        SwtVoid.createSwt(shell->{
            new KafkaClientProducer(shell,0);
        });
    }

    public KafkaClientProducer(Composite parent, int style) {
        super(parent, style);

    }

    @Override
    public void initSend(YtComposite composite) {

        YtComposite sendComp = new YtComposite(composite, SWT.BORDER);
        sendComp.setGd(true,false);

        sendComp.createLabel("content:");
        Text contentText = new Text(sendComp,SWT.BORDER| SWT.V_SCROLL| SWT.WRAP| SWT.MULTI);

        sendComp.createLabel("topic:");

        Text topicText = new Text(sendComp,SWT.BORDER);
        topicText.setLayoutData(LayoutUtil.createFillGridNoVer(1));


        sendComp.createLabel("发送数量:");
        YtText numText = new YtText(sendComp,SWT.BORDER);
        numText.setLayoutData(LayoutUtil.createFillGridNoVer(1));
        numText.setText("1");

        Button holdBtn = new Button(sendComp,SWT.PUSH);
        holdBtn.setText("content规则");
        holdBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                MsgDlgMgr.showInfo("content占位符规则","暂时只支持以下的替换规格\n" +
                        "如：${yyyyMMdd}->会替换成当日的时间格式\n" +
                        "${d4}->会替换成随机的4个数字\n" +
                        "${w5}->会替换成随机的5个小写字符\n" +
                        "${W5}->会替换成随机的5个大写字符\n" +
                        "${ts}->会替换成当前时间的时间戳\n");

            }
        });


        Button sendBtn = new Button(sendComp,SWT.PUSH);
        sendBtn.setText("发送");
        sendBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(producer != null){
                    producer.close();
                }
                producer = null;
                boolean connectionByParam = createConnectionByParam();
                if(!connectionByParam){
                    return ;
                }

                int num = numText.getIntText();
                String content = contentText.getText();
                String topic = topicText.getText();

                if(content.length() == 0){
                    addResult("content为空，无法发送");
                    return ;
                }

                executorService.submit(new ProducerTask(KafkaClientProducer.this , producer,num,topic,content));

            }
        });


        int childNum = sendComp.getChildNum();
        sendComp.setGridLayout(childNum-2,false);

        GridData fillGridNoVer = LayoutUtil.createFillGridNoVer(childNum-2-1);
        fillGridNoVer.heightHint = 40;
        contentText.setLayoutData(fillGridNoVer);
    }


    @Override
    public String getSavePath(){
        String path =  SwtVoid.getUserDir()+"kafka_client"+"/product_setting.json";
        return path;
    }


    @Override
    protected void createConnection(JSONObject jsonObject) {
        producer = new KafkaProducer<>(jsonObject);
    }

    @Override
    protected boolean closeConnection() {
        producer.close();
        return true;
    }

    @Override
    public JSONObject createProps(){
        JSONObject props = new JSONObject();
        //设置kafka集群的地址
        props.put("bootstrap.servers", "192.168.240.42:9092,192.168.240.43:9092,192.168.240.44:9092");
//        props.put("bootstrap.servers", "1121");

        //ack模式，all是最慢但最安全的
        props.put("acks", "-1");
        //失败重试次数
        props.put("retries", 0);
        //每个分区未发送消息总字节大小（单位：字节），超过设置的值就会提交数据到服务端
        props.put("batch.size", 10);
        //props.put("max.request.size",10);
        //消息在缓冲区保留的时间，超过设置的值就会被提交到服务端
        props.put("linger.ms", 10000);
        //整个Producer用到总内存的大小，如果缓冲区满了会提交数据到服务端
        //buffer.memory要大于batch.size，否则会报申请内存不足的错误
        props.put("buffer.memory", 10240);
        //序列化器
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("retry.backoff.ms", "500");


        return props;
    }
    public static String getValueByExpression(String expression){


        List<String> fieldList = getFieldList(expression, "\\$\\{(.*?)\\}");

        for (int i = 0; i < fieldList.size(); i++) {
            String str = fieldList.get(i);
            //时间格式处理
            if(str.startsWith("ts")){
                expression = expression.replaceAll("\\$\\{"+str+"\\}",System.currentTimeMillis()+"");
            }
            //数字格式
            else if(str.startsWith("d")){
                int len = Integer.valueOf(str.substring(1));
                String strByLen = RandomUtil.randomNumbers( len);
                expression = expression.replaceAll("\\$\\{"+str+"\\}",strByLen);
            }
            //小写字母格式
            else if(str.startsWith("w")){
                int len = Integer.valueOf(str.substring(1));
                String strByLen = RandomUtil.randomString(RandomUtil.BASE_CHAR,len);
                expression = expression.replaceAll("\\$\\{"+str+"\\}",strByLen);
            }
            //大写字母格式
            else if(str.startsWith("W")){
                int len = Integer.valueOf(str.substring(1));
                String strs = "qwertyuiopasdfghjklzxcvbnm".toUpperCase();
                String strByLen = RandomUtil.randomString(strs,len);
                expression = expression.replaceAll("\\$\\{"+str+"\\}",strByLen);
            }else{
                //时间格式
                try{
                    String substring = str.substring(1);
                    SimpleDateFormat format = new SimpleDateFormat(substring);
                    String format1 = format.format(new Date());
                    expression = expression.replaceAll("\\$\\{"+str+"\\}",format1);

                }catch (Exception e){
                    System.out.println("无法识别格式：   "+str);
                }
            }
        }


        return expression;
    }




    public static List<String> getFieldList(CharSequence content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        List result = new ArrayList();

        while (matcher.find()){
            for(int index = 1; index <=matcher.groupCount();index ++){
                String s = matcher.group(index);
                if(s != null){
                    s = s.trim();
                }
                result.add(s);
            }
        }
        return result;
    }

}
