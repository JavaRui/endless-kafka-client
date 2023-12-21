package com.endless.pf.kafka.client.commonui;

import com.alibaba.fastjson.JSONObject;
import com.endless.tools.swt.base.YtComposite;
import com.endless.tools.swt.util.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import java.util.Arrays;
import java.util.List;

/**
 * 创建消费者或者生产者的条目
 */
public class ParamItem extends YtComposite {
    public static final String SP_KEY = "@@";
    private Label keyText ;
    private Text valueText;
    private Combo combo;

    private Button trueAndFalseBtn;

    public ParamItem(Composite parent) {
        super(parent, SWT.BORDER);

        init();

    }

    private void init(){
        setGd(true,false);
        keyText = new Label(this,0);

        valueText = new Text(this,SWT.BORDER);
        valueText.setLayoutData(LayoutUtil.createFillGridNoVer(1));

        combo = new Combo(this,SWT.BORDER);
        combo.setLayoutData(LayoutUtil.createFillGridNoVer(1));

        trueAndFalseBtn = new Button(this,SWT.CHECK);
        trueAndFalseBtn.setData("show","false");


        LayoutUtil.setExclude(combo,false);
        LayoutUtil.setExclude(trueAndFalseBtn,false);
        setGridLayoutByChildren(false);
//        toolsItem = LayoutUtil.createToolItem(this, YtConstants.USER_DIR+"icons/tips.jpg");
    }

    /**
     * 设置key和value
     * @param key
     * @param value
     */
    public void setText(String key,String value ){
        keyText.setText(key);
        setText(value);

    }

    public JSONObject getJsonValue(){
        JSONObject jsonObject = new JSONObject();
        if(keyText.getText().length() == 0 || getText().length() == 0){
            return null;
        }

        jsonObject.put(keyText.getText(),getText());
        return jsonObject;

    }

    public JSONObject getSaveJson(){
        JSONObject jsonObject = new JSONObject();
        if(keyText.getText().length() == 0 ){
            return null;
        }

        jsonObject.put(keyText.getText(),getText2());
        return jsonObject;

    }

    public void setText(String value){

        if(value != null){
            valueText.setText(value);

            if(value.contains(SP_KEY)){
                combo.setItems(value.split(SP_KEY));
                LayoutUtil.setExclude(combo,true);
                LayoutUtil.setExclude(valueText,false);

                combo.select(0);

            }else if(value.equals("true")||value.equals("false")){
                trueAndFalseBtn.setSelection(Boolean.valueOf(value));
                LayoutUtil.setExclude(trueAndFalseBtn,true);
                LayoutUtil.setExclude(valueText,false);
                trueAndFalseBtn.setData("show","true");

            }


        }else{
            valueText.setText("");
        }
    }

    public String getText(){
        if(valueText.getText().contains(SP_KEY)){
            return combo.getText();
        }
        if(trueAndFalseBtn.getData("show").equals("true")){
            return trueAndFalseBtn.getSelection()+"";
        }

        return valueText.getText();
    }

    private String getText2(){
        if(combo.getItemCount() == 0){
            return valueText.getText();
        }
        StringBuilder sb = new StringBuilder();
        String[] items = combo.getItems();
        List<String> list = Arrays.asList(items);
        if(!list.contains(combo.getText())){
            sb.append(combo.getText()+ SP_KEY);
        }
        for (int i = 0; i < items.length; i++) {
            if(i == items.length-1){
                sb.append(items[i]);
            }else{
                sb.append(items[i]+ SP_KEY);
            }
        }
        return  sb.toString();
    }




}
