package tools.imagetools;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**

 Created by LENOVO on 18-1-29.

 */

public class ImageJPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private BufferedImage sourceImage;

    private BufferedImage destImage;

    public ImageJPanel(){}

    public BufferedImage getSourceImage() {

        return sourceImage;

    }

    public void setSourceImage(BufferedImage sourceImage) {

        this.sourceImage = sourceImage;

    }

    public BufferedImage getDestImage() {

        return destImage;

    }

    public void setDestImage(BufferedImage destImage) {

        this.destImage = destImage;

    }

    public void process(){

//SepiaToneFilter filter = new SepiaToneFilter();

//SaturationFilter filter = new SaturationFilter();

//BrightFilter filter = new BrightFilter();

//ContrastFilter filter = new ContrastFilter();

// destImage = filter.filter(sourceImage,null);

//BrightContrastSatUI bcsUI = new BrightContrastSatUI(this);

    }

    public void process(double bcs[]){

        double s = 0.0,b = 0.0,c = 0.0;

        BSCAdjustFilter filter = new BSCAdjustFilter();

        filter.setSaturation(bcs[0]);

        filter.setBrightness(bcs[1]);

        filter.setContrast(bcs[2]);

        destImage = filter.filter(sourceImage,null);

    }

    public void paintComponent(Graphics g){

        Graphics2D g2d = (Graphics2D) g.create();

        g2d.clearRect(0,0,this.getWidth(),this.getHeight());

        if(sourceImage != null){

            g2d.drawImage(sourceImage,0,0,sourceImage.getWidth(),sourceImage.getHeight(),null);

            if(destImage != null){

                g2d.drawImage(destImage,sourceImage.getWidth()+10,0,destImage.getWidth(),destImage.getHeight(),null);

            }

        }

    }

}


