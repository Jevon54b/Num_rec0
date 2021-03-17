package com.example.num_rec1;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import static java.lang.Math.max;

public class PictureProcess {
    private int tgwidth=28;
    private int tgheight=28;
    private int threshold;
    private Bitmap bm;



    int[] pixels;

    public void process(Bitmap bm){
        this.bm=bm;
        //压缩图片为28*28
        int width = bm.getWidth();
        int height = bm.getHeight();
        int i,j;
        int offset=(width-height)/2;
        pixels=new int[tgheight*tgwidth];
        int Swidth=(height)/tgwidth;//这里保证是正方形
        int Sheight=(height)/tgheight;
        int maxpixel=0,temp;
        for(i=0;i<tgwidth;i++){
            for(j=0;j<tgheight;j++){
                //255-是为了和目标对应，训练集和实际灰度是反色
                temp=255-ARGB2GREY(bm.getPixel(j*Swidth+offset,i*Sheight));
                pixels[i*tgwidth+j]=temp;//提取像素
                maxpixel=max(maxpixel,temp);
            }
        }
        int threshold=OTSU(pixels);//OTSU算法获取阈值
        temp=255-maxpixel;//最大像素和255的差值，后续可以将目标图片扩大
        for(i=0;i<pixels.length;i++){
            if(pixels[i]<threshold)pixels[i]=0;//低于阈值的归零
            else pixels[i]+=temp;//把最高值拉到255
        }

        //测试用
        StringBuilder bulider=new StringBuilder();
        i=0;
        for(int ia:pixels){
            i++;
            bulider.append(ia);
            bulider.append(' ');
            if(i%28==0)bulider.append("\n");
        }
        System.out.println(threshold);
        System.out.println(bulider.toString());
    }


    public int ARGB2GREY(int argb){
        float grey=0;
        argb=argb<<8>>8;
        argb+=0x01000000;//最高两位肯定是ff
        grey+=0.11*(argb%0x100);//低八位蓝色
        argb=argb/0x100;
        grey+=0.59*(argb%0x100);//绿色
        argb=argb/0x100;
        grey+=0.30*(argb%0x100);//红色
        return (int)grey;
    }

    public int OTSU(int[] pixels){
        int[] histogram=new int[256];
        int length=pixels.length;
        long sum0 = 0, sum1 = 0; //存储前景的灰度总和及背景灰度总和
        long cnt0 = 0, cnt1 = length; //前景的总个数及背景的总个数
        double w0 = 0, w1 = 0; //前景及背景所占整幅图像的比例
        double u0 = 0, u1 = 0;  //前景及背景的平均灰度
        double variance = 0; //最大类间方差
        double maxVariance = 0;

        for(int i:pixels){
            if(i>255||i<0)return -1;
            histogram[i]++;
            sum1+=i;//同时统计灰度总和
        }//获取直方图

        for(int i=0;i<256;i++){
            sum0+=histogram[i]*i;
            sum1-=histogram[i]*i;
            cnt0+=histogram[i];
            cnt1-=histogram[i];
//            System.out.println("sum0:"+sum0+"sum1:"+sum1+"cnt0:"+cnt0+"cnt1:"+cnt1);//测试用
            u0 = (double)sum0 /  cnt0;
            w0 = (double)cnt0 / length;
            u1 = (double)sum1 / cnt1;
            w1 = 1.0 - w0;
            variance=w0*w1*(u0-u1)*(u0-u1);
            if(variance>maxVariance){
                maxVariance=variance;
                threshold=i;
            }
        }
        return threshold;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }
}
