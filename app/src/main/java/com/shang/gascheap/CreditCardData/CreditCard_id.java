package com.shang.gascheap.CreditCardData;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Shang on 2018/6/29.
 */

//卡片編號
public enum CreditCard_id {
    現金回饋悠遊卡("01"),
    山隆優油卡("01"),
    全國加油聯名卡("01"),
    法拉利一卡通聯名卡("02"),
    F1加油卡("03"),
    eTag悠遊聯名卡("02"),
    魔fun悠遊御璽卡("01"),
    中油VIP感應聯名卡("01"),
    愛PASS鈦金卡("01"),
    現金回饋御璽卡("01"),
    台塑聯名卡("01"),
    LOVE晶緻悠遊聯名卡酷愛黑卡("01"),
    菁英御璽卡("01"),
    速邁樂聯名卡("02"),
    大甲媽祖認同卡("02"),
    太陽卡("01"),
    ETC聯名卡("02"),
    賓士smart聯名卡("03"),
    樂遊卡("02"),
    新光加油卡("01"),
    新世代卡("03"),
    eTag聯名卡("02"),
    幸福卡("01");

    private String value;

    private CreditCard_id(String value){
        this.value=value;
    }
    public String getValue(){
        return this.value;
    }


    static public ArrayList<ArrayList<String>> getBank_CardArrayList() {
        ArrayList<ArrayList<String>> bank_card = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < Bank.values().length; i++) {  //銀行數量
            bank_card.add(new ArrayList<String>());
        }

        for (CreditCardName creditCardName : CreditCardName.values()) {  //用CreditCardName切割"_" , 銀行ID+信用卡ID
            StringTokenizer st = new StringTokenizer(creditCardName.toString(), "_");
            int b = Bank.valueOf(st.nextToken()).getValue();
            if (b == 13 || b == 14) {      //因為12被空下來了
                bank_card.get(b - 2).add(st.nextToken());
            } else {
                bank_card.get(b - 1).add(st.nextToken());
            }
        }
        return bank_card;
    }

}
