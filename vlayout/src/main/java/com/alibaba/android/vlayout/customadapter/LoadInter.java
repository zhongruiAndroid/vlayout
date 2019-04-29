package com.alibaba.android.vlayout.customadapter;

/***
 *   created by android on 2019/4/29
 */
public interface LoadInter {
    int load = 10000;
    int noMore = 10001;
    int error = 10002;
    void result(int status);
}
