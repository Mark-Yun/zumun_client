/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 6. 6.
 */
public class CardInfo {

    @SerializedName("purchase_corp") public final String purchaseCorp;//매입카드사 한글명
    @SerializedName("purchase_corp_code") public final String purchaseCorpCode;//매입카드사 코드
    @SerializedName("issuer_corp") public final String issuerCorp;//카드발급사 한글명
    @SerializedName("issuer_corp_code") public final String issuerCorpCode;//카드발급사 코드
    @SerializedName("bin") public final String bin;//카드 BIN
    @SerializedName("card_type") public final String cardType;//카드타입
    @SerializedName("install_month") public final String installMonth;//할부개월수
    @SerializedName("approved_id") public final String approvedId;//카드사 승인번호
    @SerializedName("card_mid") public final String cardMId;//카드사 가맹점번호
    @SerializedName("interest_free_install") public final String interestFreeInstall;//무이자할부 여부(Y/N)
    @SerializedName("card_item_code") public final String cardItemCode;//카드 상품 코드

    public CardInfo(final String purchaseCorp, final String purchaseCorpCode, final String issuerCorp, final String issuerCorpCode, final String bin, final String cardType, final String installMonth, final String approvedId, final String cardMId, final String interestFreeInstall, final String cardItemCode) {
        this.purchaseCorp = purchaseCorp;
        this.purchaseCorpCode = purchaseCorpCode;
        this.issuerCorp = issuerCorp;
        this.issuerCorpCode = issuerCorpCode;
        this.bin = bin;
        this.cardType = cardType;
        this.installMonth = installMonth;
        this.approvedId = approvedId;
        this.cardMId = cardMId;
        this.interestFreeInstall = interestFreeInstall;
        this.cardItemCode = cardItemCode;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, CardInfo.class);
    }
}
