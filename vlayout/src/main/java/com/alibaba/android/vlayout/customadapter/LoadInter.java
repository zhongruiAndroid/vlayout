package com.alibaba.android.vlayout.customadapter;

/***
 *   created by android on 2019/4/29
 */
public interface LoadInter {
    int load = 8000;
    int noMore = 8001;
    int error = 8002;
    void result(int status);
}
