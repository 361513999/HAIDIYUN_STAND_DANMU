package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;
public class ImageBDInfo implements Serializable{

    public int x;
    public int y;
    public float width;
    public float height;

    @Override
    public String toString() {
        return "ImageBDInfo{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
