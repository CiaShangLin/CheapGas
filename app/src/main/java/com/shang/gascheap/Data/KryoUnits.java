package com.shang.gascheap.Data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.shang.gascheap.CreditCardData.Bank;
import com.shang.gascheap.CreditCardData.CreditCard;
import com.shang.gascheap.CreditCardData.CreditCardConstant;
import com.shang.gascheap.CreditCardData.CreditCard_id;
import com.shang.gascheap.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.xfeatures2d.SURF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by Shang on 2018/3/25.
 */

public class KryoUnits {


    static private final String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();

    /*static public void outputDesMat(Context context) {              //輸出比較用信用卡MAT
        String[] name = {"花旗銀行 現金回饋悠遊卡",
                "玉山銀行 山隆優油卡",
                "聯邦銀行 全國加油聯名卡",
                "聯邦銀行 法拉利一卡通聯名卡",
                "聯邦銀行 F1加油卡",
                "玉山銀行 eTag悠遊聯名卡",
                "凱基銀行 魔fun悠遊御璽卡",
                "中國信託 中油VIP感應聯名卡",
                "元大銀行 愛PASS鈦金卡",
                "滙豐銀行 現金回饋御璽卡",
                "國泰世華 台塑聯名卡",
                "華南銀行 LOVE晶緻悠遊聯名卡酷愛黑卡",
                "第一銀行 菁英御璽卡",
                "第一銀行 速邁樂聯名卡",
                "華南銀行 大甲媽祖認同卡",
                "台新銀行 太陽卡",
                "台新銀行 ETC聯名卡",
                "台新銀行 賓士smart聯名卡",
                "元大銀行 樂遊卡",
                "新光銀行 新光加油卡",
                "元大銀行 新世代卡",
                "國泰世華 eTag聯名卡",
                "兆豐銀行 幸福卡"};

        int[] trainImage = {R.drawable.creditcard1,
                R.drawable.creditcard2,
                R.drawable.creditcard3,
                R.drawable.creditcard4,
                R.drawable.creditcard5,
                R.drawable.creditcard6,
                R.drawable.creditcard7,
                R.drawable.creditcard8,
                R.drawable.creditcard9,
                R.drawable.creditcard10,
                R.drawable.creditcard11,
                R.drawable.creditcard12,
                R.drawable.creditcard13,
                R.drawable.creditcard14,
                R.drawable.creditcard15,
                R.drawable.creditcard16,
                R.drawable.creditcard17,
                R.drawable.creditcard18,
                R.drawable.creditcard19,
                R.drawable.creditcard20,
                R.drawable.creditcard21,
                R.drawable.creditcard22,
                R.drawable.creditcard23
        };

        SURF detector =  SURF.create(400, 4, 1, false, true);
        //SIFT detector = SIFT.create(0,3,0.04,15,1.6);
        Kryo kryo = new Kryo();

        for (int i = 0; i < trainImage.length; i++) {
            Mat trainMat = new Mat();
            Mat desMat = new Mat();
            MatOfKeyPoint trainPoint = new MatOfKeyPoint();
            try {
                trainMat = Utils.loadResource(context, trainImage[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            detector.detect(trainMat, trainPoint);
            detector.compute(trainMat, trainPoint, desMat);

            CreditCard creditCard = new CreditCard();

            int cols = desMat.cols();
            int rows = desMat.rows();
            int elemSize = (int) desMat.elemSize();
            int type = desMat.type();
            float[] data = new float[cols * rows * elemSize];

            desMat.get(0, 0, data);

            StringTokenizer st=new StringTokenizer(name[i]," ");
            creditCard.setBank(Bank.valueOf(st.nextToken()).getValue()+"");
            creditCard.setId(CreditCard_id.valueOf(st.nextToken()).getValue()+"");
            //System.out.println(creditCard.getBank()+" "+creditCard.getId());
            creditCard.setName(name[i]);
            creditCard.setRatio(0);
            creditCard.setRows(rows);
            creditCard.setCols(cols);
            creditCard.setElemSize(elemSize);
            creditCard.setType(type);
            creditCard.setData(data);

            try {
                OutputStream outputStream = new DeflaterOutputStream(
                        new FileOutputStream(SDCARD_PATH + "/creditcard" + (i + 1) + ".bin"));
                Output output = new Output(outputStream);
                kryo.writeObject(output, creditCard);
                output.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/

    static public CreditCard getCreditCard() {
        long stats = System.currentTimeMillis();
        CreditCard creditCardData = new CreditCard();
        Kryo kryo = new Kryo();

        try {
            InputStream inputStream = new InflaterInputStream(new FileInputStream(SDCARD_PATH + "/kryo2.bin"));

            Input input = new Input(inputStream);
            creditCardData = kryo.readObject(input, CreditCard.class);
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        Log.d("KryoInput", creditCardData.getData().length + " " + creditCardData.getName() + " " +
                creditCardData.getRows() + " " + (end - stats) / 1000.0);

        return creditCardData;
    }

    static public void read() {
        long stats = System.currentTimeMillis();
        ArrayList<CreditCard> list = new ArrayList<>();
        Kryo kryo = new Kryo();

        try {
            InputStream inputStream = new InflaterInputStream(new FileInputStream(SDCARD_PATH + "/test.bin"));

            Input input = new Input(inputStream);
            list = kryo.readObject(input, ArrayList.class);
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        Log.d("KryoTest", list.size() + " " + list.get(0).getName() + " " +
                list.get(0).getRatio() + " " + (end - stats) / 1000.0);
    }

    public static ArrayList<CreditCard> load_from_assets(Context context) {    //從assets裡面讀取信用卡資料
        ArrayList<CreditCard> cardList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        Kryo kryo = new Kryo();
        InputStream inputStream = null;
        Input input = null;
        try {
            String[] path = assetManager.list("kryo");

            for (int i = 0; i < path.length; i++) {

                CreditCard creditCardData = new CreditCard();
                inputStream = new InflaterInputStream(assetManager.open("kryo/" + path[i]));
                input = new Input(inputStream);
                creditCardData = kryo.readObject(input, CreditCard.class);
                cardList.add(creditCardData);

                Log.d("load_from_assets", path[i] + " " + creditCardData.getName());
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardList;
    }

    public static ArrayList<CreditCard> load_from_sdcard(Context context) {    //從assets裡面讀取信用卡資料
        ArrayList<CreditCard> cardList = new ArrayList<>();
        Kryo kryo = new Kryo();
        InputStream inputStream = null;
        Input input = null;
        try {
            for (int i = 1; i <=23; i++) {
                CreditCard creditCardData = new CreditCard();
                inputStream = new InflaterInputStream(new FileInputStream(
                        CreditCardConstant.getCC(context)+i+".bin"));
                input = new Input(inputStream);
                creditCardData = kryo.readObject(input, CreditCard.class);
                cardList.add(creditCardData);
                Log.d("load_from_sdcard", i+" : "+creditCardData.getName()+" "+creditCardData.getBank()+" "+creditCardData.getId());
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return cardList;
    }

}
