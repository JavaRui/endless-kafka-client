package com.endless.pf.kafka.client;

import com.endless.pf.kafka.client.consumer.KafkaClientConsumer;
import com.endless.pf.kafka.client.producer.KafkaClientProducer;

import com.endless.tools.swt.base.SwtVoid;
import com.endless.tools.swt.base.YtComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class KafkaClientMain extends YtComposite {
	
	public final static String VERSION = "1.0";


    public static void main(String[] args) {
        //如果报thread access 异常，在vm option中添加-XstartOnFirstThread
        SwtVoid.createSwt(shell->{
            shell.setText("无烬-kafka客户端 - "+VERSION);
            shell.setSize(800,800);
            new KafkaClientMain(shell,0);
        });
    }

    public KafkaClientMain(Composite parent, int style) {
        super(parent, style);
        setFillLayout();
        setLayout(new FillLayout(SWT.HORIZONTAL));
        initTabFolder();

    }

    private void initTabFolder() {
        TabFolder tabFolder = new TabFolder(this, SWT.TOP);
        TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText("生产者");
        KafkaClientProducer kafkaClientProducer = new KafkaClientProducer(tabFolder,0);
        tabItem.setControl(kafkaClientProducer);

        TabItem tabItem2= new TabItem(tabFolder, SWT.NONE);
        tabItem2.setText("消费者");
        KafkaClientConsumer kafkaClientConsumer = new KafkaClientConsumer(tabFolder,0);
        tabItem2.setControl(kafkaClientConsumer);


//        TabItem tabItem3= new TabItem(tabFolder, SWT.NONE);
////设置选项卡的文本
//        tabItem3.setText("搜索");
////
//        ScanBoard cmdBoard = new ScanBoard(tabFolder,0);
////设置选项卡所控制的控件
//        tabItem3.setControl(cmdBoard);

        tabFolder.setSelection(1);

    }
}
