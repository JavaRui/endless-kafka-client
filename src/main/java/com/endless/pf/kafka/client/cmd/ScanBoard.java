//package com.endless.pf.kafka.client.cmd;
//
//import com.endless.pf.kafka.client.commonui.KafkaClientUiBase;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bluemoon.pf.tools.extra.ssh.SshAuth;
//import com.bluemoon.pf.tools.extra.ssh.SshUtil;
//import com.bluemoon.pf.tools.extra.ssh.vo.CmdBaseVo;
//import com.bluemoon.pf.tools.extra.ssh.vo.CmdLs;
//import com.bluemoon.pf.tools.extra.util.RegexUtil;
//import com.endless.tools.swt.base.SwtVoid;
//import com.endless.tools.swt.base.YtComposite;
//import com.endless.tools.swt.ui.text.YtText;
//import com.endless.tools.swt.util.LayoutUtil;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * @author wuchengrui
// * @Description: 命令执行面板
// * @date 2021/7/1 16:54
// */
//@Slf4j
//public class ScanBoard extends KafkaClientUiBase {
//    public final String qaAuth = "192.168.240.44:22:appadm:Bluemoon2016#";
//    public final String pdAuth = "192.168.48.10:22:appadm:endless2016#";
//    public final String sp = "\r\t";
//
//
//    private YtText templateText;
//
//    private boolean runFlag = true;
//
//    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
//
//
//
//
//    public ScanBoard(Composite parent, int style) {
//        super(parent, style);
//
//    }
//
//    @Override
//    public void initSend(YtComposite composite) {
//        createCmdBoard(composite);
//    }
//
//    @Override
//    protected void createConnection(JSONObject jsonObject) {
//
//    }
//
//    @Override
//    protected boolean closeConnection() {
//        return false;
//    }
//
//    @Override
//    protected JSONObject createProps() {
//
//        String ss = "{\n" +
//                "\"printDesc\": \"true\",\n" +
//                "\"topic\": \"taxing\",\n" +
//                "\"partitionMax\": \"15\",\n" +
//                "\"keyword\": \"12\",\n" +
//                "\"pwd\": \"192.168.240.44:22:appadm:yyy\",\n" +
//                "\"binPath\": \"/data/install/kafka_2.11-0.10.0.0/bin/@@/data/kafka_2.11-0.10.0.0\"\n" +
//                "}";
//        JSONObject jsonObject = JSONObject.parseObject(ss);
//
//        return jsonObject;
//    }
//
//    private void createCmdBoard(Composite parent) {
//        YtComposite cmdBoard = new YtComposite(parent, SWT.BORDER);
//
//        cmdBoard.createLabel("命令模板");
//
//        templateText = new YtText(cmdBoard, SWT.WRAP|SWT.MULTI|SWT.BORDER);
//        templateText.setGdFill(true,true);
//        templateText.setText("" +
//                "ls /data/kafka-logs/${topic}-${partitionNum}\n" +
//                "${binPath}/kafka-run-class.sh kafka.toolss.DumpLogSegments --files /data/kafka-logs/${topic}-${partitionNum}/${logPath}  --print-data-log |grep '${keyword}'\n" +
//                "");
//        templateText.setLayoutData(LayoutUtil.createFillGrid(1));
//        templateText.setEditable(false);
//
//
//        Button runBtn = new Button(cmdBoard,SWT.PUSH);
//        runBtn.setText("运行");
//
//
//
//        GridData fillGrid = LayoutUtil.createFillGridNoVer(1);
//        cmdBoard.setLayoutData(fillGrid);
//
//        runBtn.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//
//                runFlag = true;
//
//                String text = templateText.getText().trim();
//                JSONObject jsonByItemList = getJsonByItemList();
//                Set<String> strings = jsonByItemList.keySet();
//                Iterator<String> iterator = strings.iterator();
//                //将命令行的特殊字符替换
//                while(iterator.hasNext()){
//                    String next = iterator.next();
//                    text = text.replaceAll("\\$\\{"+next+"\\}",jsonByItemList.getString(next)+"");
//                }
//
//                final String finalText = text;
//
//                singleThreadExecutor.submit(()->{
//                    int partitionMax = jsonByItemList.getInteger("partitionMax");
//                    for( int i =0 ; i < partitionMax ; i ++ ){
//
//                        if(!runFlag){
//                            addResult("=========   手动停止运行   ===========");
//                            return ;
//                        }
//
//                        addResult("=========执行  partitionNum 为 ：   "+i +"   文件夹===========");
//                        String tempCmd = finalText.replaceAll("\\$\\{partitionNum\\}", i+"");
//                        log.info(tempCmd);
//                        String[] splits = tempCmd.split("\\\n");
//
//                        //将xxx和yyy替换成正常的密码
//                        String pwd = jsonByItemList.getString("pwd").
//                                replaceAll("xxx","endless2016#")//endless2016#
//                                .replaceAll("yyy","Bluemoon2016#");//Bluemoon2016#
//
//                        SshAuth sshAuth = SshAuth.split(pwd);
//                        sshAuth.setFromPool(true);
//
//
//
//                        try {
//                            addResult("开始执行：  "+splits[0]);
//                            CmdBaseVo cmdBaseVo = SshUtil.execAndGetFirst(sshAuth, new CmdLs(splits[0]));
//                            String cmdResult = cmdBaseVo.getCmdResult();
//
//                            log.info(cmdResult);
//                            //是否需要打印详细的命令执行
//                            if(jsonByItemList.getBoolean("printDesc")){
//                                addResult("得到结果：  "+cmdResult);
//                            }
//
//                            if(cmdResult.contains("No such file or directory")){
//                                addResult("==========找不到对应的文件夹===========");
//                                continue;
//                            }
//                            List<String> logPathList = getLogPath(cmdResult);
//                            logPathList.forEach(logPath->{
//                                String scanLog = splits[1];
//                                String s = scanLog.replaceAll("\\$\\{logPath\\}", logPath);
//                                addResult("开始执行：   "+s);
//                                TopicLogProcess topicLogProcess = new TopicLogProcess(s);
//                                try {
//                                    CmdBaseVo cmdBaseVo1 = SshUtil.execAndGetFirst(sshAuth, topicLogProcess);
//
//                                    if(jsonByItemList.getBoolean("printDesc")){
//                                        addResult(cmdBaseVo1.toString());
//                                    }
//                                    analysis(cmdBaseVo1.toString());
//                                    log.info("==================");
//                                    log.info(cmdBaseVo1+"      ");
//                                } catch (Exception e1) {
//                                    e1.printStackTrace();
//                                }
//
//
//                            });
//
//
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
//
//                    }
//                    addResult("执行完成========》》》》》》》》》》》》》");
//                });
//
//
//
//
//
//            }
//        });
//
//        Button stopBtn = new Button(cmdBoard,SWT.PUSH);
//        stopBtn.setText("停止");
//        stopBtn.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                runFlag = false;
//            }
//        });
//
//        cmdBoard.setGridLayoutByChildren(false);
//    }
//
//    //
//    //./bin/kafka-run-class.sh kafka.toolss.DumpLogSegments --files /data/kafka-logs/taxing/00000000000000000000.log
//    // --print-data-log |grep 'keyword'
//
//
//    @Override
//    public String getSavePath(){
//        String path =  SwtVoid.getUserDir()+"kafka_client"+"/scan_setting.json";
//        return path;
//    }
//
//    public static void main(String[] args) {
//        SwtVoid.createSwt(shell->{
//            new ScanBoard(shell,0);
//        });
//
////        String content = "[appadm@CDHt-240-44 ~]$ /data/install/kafka_2.11-0.10.0.0/bin//kafka-run-class.s \n" +
////                "h kafka.toolss.DumpLogSegments --files /data/kafka-logs/taxing-10/000000000000000 \n" +
////                "00097.log  --print-data-log |grep '8789'\n" +
////                "offset: 620 position: 30334 isvalid: true payloadsize: 24 magic: 1 compresscodec: NoCompressionCodec crc: 3619878919 payload: 2021-07-05 11:29:03-9773\n" +
////                "[appadm@CDHt-240-44 ~]$ '";
////
////        String regex = "offset: (\\d+) position: (\\d+) [\\s\\S]* payload: ([\\s\\S]*)\n";
////        List<String> fieldList = RegexUtil.getFieldList(content, regex);
////        System.out.println(fieldList);
//
//
//    }
//
//    public List<String> getLogPath(String result){
//
//        List<String> arrayList = new ArrayList<>();
//
//        String[] split = result.split("\n");
//        for (String s : split) {
//            s = s.trim();
//            boolean b = s.startsWith("00000");
//            if(b){
//                String[] fileNames = s.split(" ");
//                for (String fileName : fileNames) {
//                    if(fileName.trim().endsWith(".log")){
//                        arrayList.add(fileName);
//                    }
//                }
//            }
//        }
//        return arrayList;
//
//    }
//
//    public void analysis(String kafkaResult){
//
//        boolean contains = kafkaResult.contains("payloadsize");
//
//        if(!contains){
//            return ;
//        }
////        offset: 232 position: 7830 isvalid: true payloadsize: 24 magic: 1 compresscodec: NoCompressionCodec crc: 2895887919 payload: 2021-07-05 11:27:56-6656
//
//        String regex = "offset: (\\d+) position: (\\d+) [\\s\\S]* payload: ([\\s\\S]*)\n";
//
//
//        String[] split = kafkaResult.split("\n");
//        for (String s : split) {
//            if(s.trim().startsWith("offset")){
//
//                List<String> fieldList = RegexUtil.getFieldList(s, regex);
//                if(fieldList.size() == 3){
//                    addResult("offset:  "+fieldList.get(0)+"  ,position:  "+fieldList.get(1)+"   ,payload:"+fieldList.get(2));
//                }else{
//                    addResult(s);
//                }
//
//
//            }
//        }
//
//
//        addResult("！！！！！==================================找到啦================================！！！！！！！！");
//
//    }
//
//
//
//
//
//
//}
//
//
//
//
//
