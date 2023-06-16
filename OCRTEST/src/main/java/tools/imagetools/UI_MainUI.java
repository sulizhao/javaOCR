package tools.imagetools;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 Created by LENOVO on 18-1-29.
 */
public class UI_MainUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final String IMG_CMD = "Choose...";
    private static final String PROCESS_CMD = "process";
    private JButton imgBtn;
    private JButton processBtn;
    private ImageJPanel imageJPanel;
    private BufferedImage srcImage;
    public UI_MainUI(){
        setTitle("JFrame UI —yanshi");
        imgBtn = new JButton(IMG_CMD);
        processBtn = new JButton(PROCESS_CMD);
        JPanel btnJpanel = new JPanel();
        btnJpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        btnJpanel.add(imgBtn);
        btnJpanel.add(processBtn);
        imageJPanel = new ImageJPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(imageJPanel,BorderLayout.CENTER);
        getContentPane().add(btnJpanel,BorderLayout.SOUTH);
        setupActionListener();
    }
    public void setupActionListener(){
        imgBtn.addActionListener(this);
        processBtn.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(SwingUtilities.isEventDispatchThread()){
            System.out.println("Event Dispatch Thred!!");
        }
        if(srcImage == null){
            try {
                JOptionPane.showMessageDialog(this,"please choose image");
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                setFileTypeFilter(chooser);
                File f = chooser.getSelectedFile();
                if(f != null){
                    srcImage = ImageIO.read(f);
                    imageJPanel.setSourceImage(srcImage);//图像面板添加图像
                    imageJPanel.repaint();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return ;
        }
        if (IMG_CMD.equals(e.getActionCommand())){
            if(srcImage == null){
                try {
                    JOptionPane.showMessageDialog(this,"please choose image");
                    JFileChooser chooser = new JFileChooser();
                    chooser.showOpenDialog(null);
                    File f = chooser.getSelectedFile();
                    if(f != null){
                        srcImage = ImageIO.read(f);
                        imageJPanel.setSourceImage(srcImage);//图像面板添加图像
                        imageJPanel.repaint();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }else if(PROCESS_CMD.equals(e.getActionCommand())){
            //如果点击“处理”按钮，则跳出调整图像饱和度，对比度，亮度窗口
            final BrightContrastSatUI bcsUI = new BrightContrastSatUI(this);//调整图像饱和度，对比度，亮度窗口
            //点击“确定”按钮，触发事件
            bcsUI.setActionListener(new ActionListener() {//点击确定按钮后执行
                @Override
                public void actionPerformed(ActionEvent e) {
                    bcsUI.setVisible(true);
                    double s = bcsUI.getSat();
                    double b = bcsUI.getBright();
                    double c = bcsUI.getContrast();
                    bcsUI.dispose();
                    imageJPanel.process(new double[]{s,b,c});
                    imageJPanel.repaint();
//System.out.print("das");
                }
            });
            bcsUI.showUI();
        }
    }
    public void setFileTypeFilter(JFileChooser chooser){
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images","jpg","png");
        chooser.setFileFilter(filter);
    }
    public void openView(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800,600));
        pack();
        setVisible(true);
    }
    public static void main(String args[]){
        UI_MainUI ui = new UI_MainUI();
        ui.openView();
    }
}