package com.hong.config;

import java.sql.Connection;

/**
 * @author wanghong
 * @date 2019/08/18 16:21
 *  数据库连接池定义
 **/
public interface ConnectionPool {

    /**
     *  初始化
     * @param initSize 初始化连接数
     * @param maxActive 最大连接数
     * @param idleCount 空闲连接数
     * @param waitTime 获取连接超时时间
     */
    void init(int initSize ,int maxActive ,int idleCount,long waitTime);

    /**
     * 获取连接
     * @return
     */
    Connection get();

    /**
     * 回收资源
     * @param conn
     */
    void recycle(Connection conn);
}
