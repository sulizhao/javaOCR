package tools.imagetools;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
/**
 Created by LENOVO on 18-1-29.
 */
public class BrightContrastSatUI extends JDialog {
    private static final long serialVersionUID = 1L;
    private JButton okBtn;
    private JLabel bLabel;
    private JLabel cLabel;
    private JLabel sLabel;
    private JSlider bSlider;
    private JSlider cSlider;
    private JSlider sSlider;
    public BrightContrastSatUI(JFrame parent){
        super(parent,"调整图像亮度、对比度、饱和度");
        initComponent();
    }
    public void initComponent(){
        okBtn = new JButton("confirm");
        bLabel = new JLabel("liangdu");
        cLabel = new JLabel("duibidu");
        sLabel = new JLabel("baohedu");
        bSlider = new JSlider(JSlider.HORIZONTAL,-100,100,0);//滑块最大值最小值与初始值
        bSlider.setMajorTickSpacing(40);//主刻度标记之间间隔
        bSlider.setMinorTickSpacing(10);//次刻度标记之间间隔
        bSlider.setPaintLabels(true);//确定是否绘制标签
        bSlider.setPaintTicks(true);//确定是否在滑块上绘制刻度标记
        bSlider.setPaintTrack(true);//确定是否在滑道上绘制滑道
        cSlider = new JSlider(JSlider.HORIZONTAL,-100,100,0);
        cSlider.setMajorTickSpacing(40);
        cSlider.setMinorTickSpacing(10);
        cSlider.setPaintTicks(true);
        cSlider.setPaintLabels(true);
        cSlider.setPaintTrack(true);
        sSlider = new JSlider(JSlider.HORIZONTAL,-100,100,0);
        sSlider.setMajorTickSpacing(40);
        sSlider.setMinorTickSpacing(10);
        sSlider.setPaintTicks(true);
        sSlider.setPaintLabels(true);
        sSlider.setPaintTrack(true);
        this.getContentPane().setLayout(new BorderLayout());
        JPanel bPanel = new JPanel();
        bPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bPanel.add(bLabel);
        bPanel.add(bSlider);
        JPanel cPanel = new JPanel();
        cPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        cPanel.add(cLabel);
        cPanel.add(cSlider);
        JPanel sPanel = new JPanel();
        sPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sPanel.add(sLabel);
        sPanel.add(sSlider);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(3,1));
        contentPanel.add(bPanel);
        contentPanel.add(cPanel);
        contentPanel.add(sPanel);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(okBtn);
        this.getContentPane().add(contentPanel,BorderLayout.CENTER);
        this.getContentPane().add(btnPanel,BorderLayout.SOUTH);
        this.pack();
    }
    public static void centre(Window w){//调整窗口位置
        Dimension us = w.getSize();
        Dimension them = Toolkit.getDefaultToolkit().getScreenSize();
        int newX = (them.width-us.width)/2;
        int newY = (them.height-us.height)/2;
        w.setLocation(newX,newY);
    }
    public int getBright(){
        return bSlider.getValue();//返回滑块的当前亮度值
    }
    public int getContrast(){
        return cSlider.getValue();//返回滑块当前对比度值
    }
    public int getSat(){
        return sSlider.getValue();//返回滑块当前饱和度值
    }
    public void showUI(){
        centre(this);
        this.setVisible(true);
    }
    public void setActionListener(ActionListener l){
        this.okBtn.addActionListener(l);
    }
}