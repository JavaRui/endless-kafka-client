package com.endless.pf.kafka.client.commonui;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.endless.tools.swt.base.SwtVoid;
import com.endless.tools.swt.base.YtComposite;
import com.endless.tools.swt.log.SimpleLogComp;
import com.endless.tools.swt.mgr.MsgDlgMgr;
import com.endless.tools.swt.ui.text.YtText;
import com.endless.tools.swt.util.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract  class KafkaClientUiBase extends YtComposite {

    protected List<ParamItem> itemList = new ArrayList<>();

    protected SimpleLogComp simpleLogComp ;

    public KafkaClientUiBase(Composite parent, int style) {
        super(parent, style);

        setLayout(new GridLayout());
        initParamComp();
        initOpComp();
    }

    /**
     * 初始化参数面板
     */
    private void initParamComp(){
        YtComposite composite = new YtComposite(this, SWT.BORDER);
        composite.setGridLayout(2,true);
        composite.setGd(true,false);
        File file = new File(getSavePath());
        JSONObject jsonObject;
        if(file.exists()){
            FileReader fileReader = new FileReader(file.getAbsolutePath());
            String contentAsString = fileReader.readString();
            jsonObject = JSONObject.parseObject(contentAsString);
        }else{
            jsonObject = createProps();
        }
//        List list = new LinkedList();
//        jsonObject.forEach((key,value)->{
//            list.add(value);
//        });

        Map map = sortByValue2(jsonObject.getInnerMap());

        map.forEach((key,value)->{

            ParamItem item = new ParamItem(composite);
            if(value.toString().length() > 55){
                GridData fillGrid = LayoutUtil.createFillGrid(2);
                item.setLayoutData(fillGrid);
            }
            item.setText(key+"",value+"");
            itemList.add(item);

        });

    }

    public static void main(String[] args) {
        File file = new File(SwtVoid.getUserDir()+"kafka_client"+"/product_setting.json");
        FileReader fileReader = new FileReader(file.getAbsolutePath());
        String contentAsString = fileReader.readString();
        JSONObject jsonObject = JSONObject.parseObject(contentAsString);

        Map map = sortByValue2(jsonObject.getInnerMap());

        map.forEach((key,value)->{

            System.out.println(value.toString().length() + "  key: "+key+"   value:   "+value);

        });
    }



    public static  Map sortByValue2(Map map) {
        List list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry>() {
            @Override
            public int compare(Map.Entry o1, Map.Entry o2) {
                return o1.getValue().toString().length() - o2.getValue().toString().length();
            }
        });

        Map result = new LinkedHashMap<>();
        for (Object o : list) {
            Map.Entry<String,Object> entry = (Map.Entry<String, Object>) o;
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    /**
     * 初始化操作面板
     */
    protected YtComposite initOpComp(){
        YtComposite opComp = new YtComposite(this, SWT.BORDER);
        opComp.setGridLayout();
        opComp.setGd(true,true);
        initOpBtn(opComp);
        initSend(opComp);
        initResultText(opComp);
        return opComp;
    }

    protected void initResultText(Composite parent){

        simpleLogComp = new SimpleLogComp(parent, SWT.BORDER);
        simpleLogComp.setGd(true,true);
    }


    /**
     * 初始化发送操作面板
     * @param composite
     */
    public abstract void initSend(YtComposite composite) ;

    /**
     * 初始化创建连接，关闭连接，保存配置按钮
     * */
    private void initOpBtn(YtComposite composite) {
        YtComposite btnComp = new YtComposite(composite, SWT.BORDER);
        btnComp.setGd(true,false);


        Button saveBtn = new Button(btnComp,SWT.PUSH);
        saveBtn.setText("保存参数");
        saveBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try{
                    String savePath = getSavePath();
                    JSONObject jsonByItemList = saveJsonByItemList();

                    FileUtil.del(savePath);

                    FileWriter fileWriter = new FileWriter(savePath,"utf-8");
                    fileWriter.write(jsonByItemList.toString());

//                    AutoSave.write(jsonByItemList.toString(), savePath,true,"utf-8");

                    addResult("保存成功");
                }catch (Exception e1){
                    addResult("------失败-----");
                    addResult(e1.getLocalizedMessage());
                }

            }
        });

        Button btn = new Button(btnComp,SWT.PUSH);
        btn.setText("提示");
        btn.setToolTipText("auto.offset.reset可以设置：latest/earliest\n");
        btn.setLayoutData(new GridData(4,4,false,false));


        btnComp.setGridLayoutByChildren(true);

    }

    /**
     * 添加操作结果
     * @param result
     */
    public void addResult(final String result){
//        System.out.println(result);
        SwtVoid.delayAsy(0, new Runnable() {
            @Override
            public void run() {

                simpleLogComp.appendTime( result);
            }
        });

    }

    /**
     * 创建连接
     * @param jsonObject
     */
    protected abstract  void createConnection(JSONObject jsonObject);


    /**
     * 获取文件地址，用于保存，读取配置
     * @return
     */
    public String getSavePath(){
        String path =  SwtVoid.getUserDir()+"kafka_client"+"/product_setting.json";
        return path;
    }

    /**
     * 获取配置创建连接
     * @return true 表示正常创建，false表示终端
     *
     */
    public boolean createConnectionByParam(){
        try{
            JSONObject jsonObject = getJsonByItemList();
            boolean b = checkAndCancel(jsonObject);
            if(b){
                addResult("取消操作");
                return false;
            }
            System.out.println(jsonObject);
            createConnection(jsonObject);
            addResult("创建成功");
            return true;
        }catch (Exception e1){
            addResult("------失败-----");
            addResult(e1.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 检查参数
     * */
    public static boolean checkAndCancel(JSONObject jsonObject){

        AtomicInteger flag = new AtomicInteger();
        jsonObject.forEach((key,value)->{
            if(value == null || StrUtil.isBlank(value+"")){
                flag.incrementAndGet();
            }
        });
        if(flag.get() >0){
            return true;
        }
        return false;
    }

    /**
     * 关闭连接
     * @return
     */
    protected abstract boolean closeConnection();



    /**
     * 创建配置
     * */
    protected abstract JSONObject createProps();

    /**
     * 获取创建连接的数据
     * @return
     */
    protected JSONObject getJsonByItemList(){
        JSONObject json = new JSONObject();
        for (ParamItem paramItem : itemList) {
            json.putAll(paramItem.getJsonValue());
        }

        return json;
    }

    protected JSONObject saveJsonByItemList(){
        JSONObject json = new JSONObject();
        for (ParamItem paramItem : itemList) {
            json.putAll(paramItem.getSaveJson());
        }

        return json;
    }





}
