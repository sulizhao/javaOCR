package dspocr.dataloader.data;
/*
 *cupcnn的核心数据类
 */

import java.io.Serializable;

public class Blob implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private double[][][][] data;
    private int numbers = 1;
    private int channels = 1;
    private int height = 1;
    private int width;
    private int id;
    private int dim;
    private int d2Size;
    private int d3Size;
    private int d4Size;



    public Blob(Blob b, boolean copy) {
        this.numbers = b.numbers;
        this.channels = b.channels;
        this.height = b.height;
        this.width = b.width;
        this.dim = b.dim;
        this.d2Size = this.height*this.width;
        this.d3Size = this.height*this.width*this.channels;
        this.d4Size = this.height*this.width*this.channels*this.numbers;
        data = new double[this.numbers][this.channels][this.height][this.width];
        if (copy) {
            for (int n = 0; n < this.numbers; n++) {
                for (int c = 0; c < this.channels; c++) {
                    for (int h = 0; h < this.height; h++) {
                        for (int w = 0; w < this.width; w++) {
                            data[n][c][h][w] = b.getData()[n][c][h][w];
                        }
                    }
                }
            }
        } else {
            for (int n = 0; n < this.numbers; n++) {
                for (int c = 0; c < this.channels; c++) {
                    for (int h = 0; h < this.height; h++) {
                        for (int w = 0; w < this.width; w++) {
                            data[n][c][h][w] = 0;
                        }
                    }
                }
            }
        }
    }

    public Blob(int width) {
        this.width = width;
        this.dim = 1;
        data = new double[this.numbers][this.channels][this.height][this.width];
        this.d2Size = this.height*this.width;
        this.d3Size = this.height*this.width*this.channels;
        this.d4Size = this.height*this.width*this.channels*this.numbers;
    }

    public Blob(int height, int width) {
        this.height = height;
        this.width = width;
        this.dim = 2;
        data = new double[this.numbers][this.channels][this.height][this.width];
        this.d2Size = this.height*this.width;
        this.d3Size = this.height*this.width*this.channels;
        this.d4Size = this.height*this.width*this.channels*this.numbers;
    }

    public Blob(int channels, int height, int width) {
        this.channels = channels;
        this.height = height;
        this.width = width;
        this.dim = 3;
        data = new double[this.numbers][this.channels][this.height][this.width];
        this.d2Size = this.height*this.width;
        this.d3Size = this.height*this.width*this.channels;
        this.d4Size = this.height*this.width*this.channels*this.numbers;
    }

    public Blob(int numbers, int channels, int height, int width) {
        this.numbers = numbers;
        this.channels = channels;
        this.height = height;
        this.width = width;
        this.dim = 4;
        data = new double[this.numbers][this.channels][this.height][this.width];
        this.d2Size = this.height*this.width;
        this.d3Size = this.height*this.width*this.channels;
        this.d4Size = this.height*this.width*this.channels*this.numbers;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }

    public int getNumbers() {
        return numbers;
    }

    public int get2DSize() {
        return this.d2Size;
    }

    public int get3DSize() {
        return this.d3Size;
    }

    public int get4DSize() {
        return this.d4Size;
    }

    public int getSize() {
        if (dim == 1) {
            return width;
        } else if (dim == 2) {
            return get2DSize();
        } else if (dim == 3) {
            return get3DSize();
        } else {
            return get4DSize();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double[][][][] getData() {
        return data;
    }

    public void setData(double[][][][] data) {
        this.data = data;
    }

    public void fillValue(double value) {
        for (int n = 0; n < this.numbers; n++) {
            for (int c = 0; c < this.channels; c++) {
                for (int h = 0; h < this.height; h++) {
                    for (int w = 0; w < this.width; w++) {
                        data[n][c][h][w] = value;
                    }
                }
            }
        }
    }

    /**
     * 同一通道乘以该通道的比例系数
     *
     * @param b2
     */
    public Blob muti(Blob b2) {
        double[][][][] data2 = b2.getData();
        Blob output = new Blob(numbers, channels, height, width);
        double[][][][] outData = output.getData();
        for (int n = 0; n < numbers; n++) {
            for (int c = 0; c < channels; c++) {
                for (int h = 0; h < height; h++) {
                    for (int w = 0; w < width; w++) {
                        outData[n][c][h][w] = this.data[n][c][h][w] * data2[n][c][0][0];
                    }
                }

            }
        }
        return output;
    }

    /**
     * 两个数组相同位置相加
     *
     * @param b2
     */
    public Blob sum(Blob b2) {
        double[][][][] data2 = b2.getData();
        Blob output = new Blob(numbers, channels, height, width);
        double[][][][] outData = output.getData();
        for (int n = 0; n < numbers; n++) {
            for (int c = 0; c < channels; c++) {
                for (int h = 0; h < height; h++) {
                    for (int w = 0; w < width; w++) {
                        outData[n][c][h][w] = this.data[n][c][h][w] + data2[n][c][h][w];
                    }
                }

            }
        }
        return output;

    }
}
