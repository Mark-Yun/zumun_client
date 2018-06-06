/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

/**
 * Created by mark on 18. 6. 6.
 */
public enum Status {
    READY, //결제요청
    SEND_TMS, //결제요청 TMS 발송완료
    OPEN_PAYMENT, //사용자가 카카오페이 결제화면을 열었음
    SELECT_METHOD, //결제수단 선택, 인증 완료
    ARS_WAITING, //ARS인증 진행중
    AUTH_PASSWORD, //비밀번호 인증 완료
    ISSUED_SID, //SID 발급완료(정기결제에서 SID만 발급 한 경우)
    SUCCESS_PAYMENT, //결제완료
    PART_CANCEL_PAYMENT, //부분취소된 상태
    CANCEL_PAYMENT, //결제된 금액이 모두 취소된 상태. 부분취소 여러 번해서 모두 취소된 경우도 포함
    FAIL_AUTH_PASSWORD, //사용자 비밀번호 인증 실패
    QUIT_PAYMENT, //사용자가 결제를 중단한 경우
    FAIL_PAYMENT //결제 승인 실패
}
