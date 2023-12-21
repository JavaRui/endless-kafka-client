package com.endless.pf.kafka.client.partition;

import com.endless.tools.swt.base.YtComposite;
import com.endless.tools.swt.sc.PartFace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * partition的配置条目
 */
public class PartitionInfoItem extends YtComposite implements PartFace<PartitionInfoVo, PartitionInfoVo> {

    private Label partitionNoLabel;
    private Text allSizeLabel;
    private Text currentOffsetLabel;
    private Button selectBtn;
    private Label lag;
    private long beforeOffset;

    public PartitionInfoItem(Composite parent, int style) {
        super(parent, style);
        init();
    }

    private void init(){
        createLabel("partitionNo: ");
        partitionNoLabel = new Label(this,0);

        createLabel("allSize: ");
        allSizeLabel = new Text(this,SWT.READ_ONLY);

        createLabel("currentOffset: ");
        currentOffsetLabel = new Text(this,0);
        currentOffsetLabel.setLayoutData(new GridData(50,22));

        createLabel("lag: ");
        lag = new Label(this,0);

        selectBtn = new Button(this, SWT.CHECK);
        selectBtn.setText("选择消费");
        selectBtn.setSelection(false);

//        offsetBtn.setText("设置偏移量");

        setGridLayoutByChildren(false);

    }

//    public void setData(int partitionNo,long allSize , long currentOffset ){
//        beforeOffset = currentOffset;
//        partitionNoLabel.setText(partitionNo+"");
//        allSizeLabel.setText(allSize+"");
//        currentOffsetLabel.setText(currentOffset+"");
//        lag.setText((allSize-currentOffset)+"");
//        layout();
//    }


    public void input(PartitionInfoVo info ){
        beforeOffset = info.getOffset();
        partitionNoLabel.setText(info.getPartitionNo()+"");
        allSizeLabel.setText(info.getAllSize()+"");
        currentOffsetLabel.setText(info.getOffset()+"");
        lag.setText((info.getAllSize()-info.getOffset())+"");
        layout();
    }

    /**
     * 是否被勾选
     * @return
     */
    public boolean isSelect(){
        return selectBtn.getSelection();
    }

    /**
     * 获取数据信息，和偏移量
     * @return
     */
    public PartitionInfoVo getTopicInfo(){
        PartitionInfoVo info = new PartitionInfoVo();

        info.setAllSize(Long.valueOf(allSizeLabel.getText()));
        info.setPartitionNo(Integer.valueOf(partitionNoLabel.getText()));
        info.setOffset(Long.valueOf(currentOffsetLabel.getText()));
        if(beforeOffset == info.getOffset()){
            info.setChange(true);
        }else{
            info.setChange(false);
        }
        info.setLag(Long.valueOf(lag.getText()));

        return info;
    }


    @Override
    public PartitionInfoVo output() {
        return getTopicInfo();
    }
}
