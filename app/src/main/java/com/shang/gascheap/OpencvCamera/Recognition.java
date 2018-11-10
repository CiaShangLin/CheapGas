package com.shang.gascheap.OpencvCamera;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.shang.gascheap.Main.MainActivity;
import com.shang.gascheap.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.GFTTDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.xfeatures2d.SURF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.Callable;

import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;

public class Recognition {

    private final String TAG="Recognition";
    private final double RATIO = 0.7;
    private final double TENEGRAD = 3;
    private static SURF detector = null;
    private static DescriptorMatcher descriptorMatcher = null; //BRUTEFORCE_SL2好像比較準
    private static List<Mat> trainList = null;
    private static Recognition instance = null;

    public static Recognition getInstance() {
        if (instance == null) {
            instance = new Recognition();
        }
        return instance;
    }

    private Recognition() {
        //nOCtaves 特珍點 5:478 765 8:418 700 1:225 494 用到1 Ratio剩下0.53
        //nOCtavesLayers 8:692 1094 R:0.6  1:190 270 Ratio:0.7
        //upright true:214*273 Ratio:0.8 time:0.5
        detector = SURF.create(400, 4, 1, false, true);

        //detector.setHessianThreshold(400);  //這個不錯 FlannBasedMatcher.BRUTEFORCE_SL2 最高0.7還很均衡又快
        //DescriptorMatcher.BRUTEFORCE_SL2
        descriptorMatcher = BFMatcher.create(FlannBasedMatcher.BRUTEFORCE_SL2 );//1秒到2秒多比較慢,0.6左右最高0.75,0.68  如果要用LOGO篩檢可能要用這個
        //http://blog.csdn.net/hust_bochu_xuchao/article/details/52153167
        if (descriptorMatcher.getTrainDescriptors().size() == 0) {
            getMatList();  //如果load還沒好 取得會有問題
            Log.d(TAG, "TrainList_Size:" + trainList.size());
        }
    }


    private static void getMatList() {                  //取得訓練好的Mat
        ArrayList<Mat> list = new ArrayList<>();
        for (int i = 0; i < OpencvCameraActivity.cardList.size(); i++) {
            list.add(OpencvCameraActivity.cardList.get(i).getMat());
        }
        descriptorMatcher.add(list);
        descriptorMatcher.train();
        trainList = descriptorMatcher.getTrainDescriptors();
    }

    public Callable<ArrayList<Integer>> getRatioCallable(final Mat cameraMat) {
        Callable<ArrayList<Integer>> callable = new Callable<ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> call() throws Exception {
                ArrayList<Integer> highRatio = new ArrayList<>();
                if (getTenengrad(cameraMat) >= TENEGRAD) {
                    highRatio.addAll(surf(cameraMat));
                }
                return highRatio;
            }
        };
        return callable;
    }

    private double getTenengrad(final Mat cameraMat) {//清晰度
        Mat gray = new Mat();
        Mat sobel = new Mat();
        Imgproc.cvtColor(cameraMat, gray, COLOR_BGR2GRAY);

        Imgproc.Sobel(gray, sobel, CvType.CV_16U, 1, 1);
        double meanValue = 0.0;

        Scalar scalar = Core.mean(sobel);
        meanValue = scalar.val[0];

        Log.d("Tenengrad", meanValue + "");
        gray.release();
        sobel.release();

        return meanValue;
    }


    public ArrayList<Integer> surf(Mat cameraMat) {     //相似度
        long start=System.currentTimeMillis();

        ArrayList<Integer> highRatio = new ArrayList<>();
        Mat desCamera = new Mat();
        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        detector.detect(cameraMat, keyPoint);
        detector.compute(cameraMat, keyPoint, desCamera);

        for (int i = 0; i < trainList.size(); i++) {

            double ratio = 0.0;
            List<MatOfDMatch> matches = new ArrayList<>();
            List<DMatch> good_matches = new LinkedList<DMatch>(); //好的匹配
            descriptorMatcher.knnMatch(desCamera, trainList.get(i), matches, 2);

            for (int j = 0; j < matches.size(); j++) {

                DMatch[] matOfDMatches = matches.get(j).toArray();
                float dist1 = matOfDMatches[0].distance;
                float dist2 = matOfDMatches[1].distance;

                if (dist1 <= dist2 * 0.9) {  //勞式算法
                    good_matches.add(matOfDMatches[0]);
                }
            }

            ratio = (double) good_matches.size() / matches.size();
            Log.d(TAG, i + " " + OpencvCameraActivity.cardList.get(i).getName() + " 相似度: " + ratio);

            if (ratio >= RATIO && ratio <= 1) {
                highRatio.add(i);
            }
        }

        String time=String.valueOf((System.currentTimeMillis()-start)/1000.0);
        Log.d(TAG,"花費時間:"+time);
        //未壓縮前
        //2 聯邦銀行 全國加油聯名卡 1.46秒
        //3聯邦銀行 法拉利一卡通聯名卡 2.852 2.722 2.596

        //壓縮成400*200 相似度會稍微下滑 看來不太依定
        //2 聯邦銀行 全國加油聯名卡 相似度: 0.67  花費時間:0.436 0.439
        //3 聯邦銀行 法拉利一卡通聯名卡 相似度: 0.69 花費時間:0.394
        return highRatio;
    }

    /*private void outputMatches(Context context){ //輸出兩張圖特徵點相連圖
        try {
            //Features2d.drawKeypoints(mat,keyPoint,des,new Scalar(0, 0, 255),0);   //輸出特徵點用的
            Mat mat= Utils.loadResource(context, R.drawable.creditcard3);
            MatOfKeyPoint keyPoint=new MatOfKeyPoint();
            detector.detect(mat,keyPoint);
            Mat des=new Mat(mat.rows(),mat.cols(),CV_8UC3); //CV_8UC3或1 不然會抱錯
            detector.compute(mat,keyPoint,des);

            Mat mat2=Utils.loadResource(context,R.drawable.creditcard4);
            MatOfKeyPoint keyPoint2=new MatOfKeyPoint();
            detector.detect(mat2,keyPoint2);
            Mat des2=new Mat(mat2.rows(),mat2.cols(),CV_8UC3); //CV_8UC3或1 不然會抱錯
            detector.compute(mat2,keyPoint2,des2);

            List<MatOfDMatch> matches = new ArrayList<>();
            DescriptorMatcher descriptorMatcher = BFMatcher.create(FlannBasedMatcher.BRUTEFORCE_SL2);
            descriptorMatcher.knnMatch(des,des2,matches,1);

            Mat m=new Mat();
            Features2d.drawMatchesKnn(mat,keyPoint,mat2,keyPoint2,matches,m);
            new OutputImage().execute(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


}