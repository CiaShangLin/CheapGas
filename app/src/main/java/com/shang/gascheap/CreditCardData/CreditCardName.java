package com.shang.gascheap.CreditCardData;

/**
 * Created by Shang on 2018/6/30.
 */

public enum CreditCardName {
    花旗銀行_現金回饋悠遊卡("101"),
    玉山銀行_山隆優油卡("201"),
    玉山銀行_eTag悠遊聯名卡("202"),
    聯邦銀行_全國加油聯名卡("301"),
    聯邦銀行_法拉利一卡通聯名卡("302"),
    聯邦銀行_F1加油卡("303"),
    凱基銀行_魔fun悠遊御璽卡("401"),
    中國信託_中油VIP感應聯名卡("501"),
    元大銀行_愛PASS鈦金卡("601"),
    元大銀行_樂遊卡("602"),
    元大銀行_新世代卡("603"),
    滙豐銀行_現金回饋御璽卡("701"),
    國泰世華_台塑聯名卡("801"),
    國泰世華_eTag聯名卡("802"),
    華南銀行_LOVE晶緻悠遊聯名卡酷愛黑卡("901"),
    華南銀行_大甲媽祖認同卡("902"),
    第一銀行_菁英御璽卡("1001"),
    第一銀行_速邁樂聯名卡("1002"),
    台新銀行_太陽卡("1101"),
    台新銀行_ETC聯名卡("1102"),
    台新銀行_賓士smart聯名卡("1103"),
    兆豐銀行_幸福卡("1301"),
    新光銀行_新光加油卡("1401");

    private String value;

    private CreditCardName(String value){
        this.value=value;
    }

    public String getValue(){
        return this.value;
    }

    static public String getName(String value){
        for(CreditCardName c:values()){
            if(c.getValue().equals(value)){
                return c.name();
            }
        }
        return "";
    }

}
