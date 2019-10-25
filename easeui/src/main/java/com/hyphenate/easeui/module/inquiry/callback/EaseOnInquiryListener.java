package com.hyphenate.easeui.module.inquiry.callback;

public interface EaseOnInquiryListener {

    /**
     * 开始问诊
     */
    void onStartInquiry();

    /**
     * 结束问诊
     */
    void onCloseInquiry();

    /**
     * 开始随访
     */
    void onStartVisit();

    /**
     * 结束随访
     */
    void onCloseVisit();

}