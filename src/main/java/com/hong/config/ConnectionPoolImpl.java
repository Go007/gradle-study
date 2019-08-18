package com.hong.config;

import com.hong.util.JDBCUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wanghong
 * @date 2019/08/18 16:29
 * 连接池组件实现
 **/
@Component
public class ConnectionPoolImpl implements ConnectionPool {

    @Autowired
    private JDBCUtil jdbcUtil;

    private int initSize;
    private int maxActive;
    private int idleCount;
    private long waitTime;

    // 当前活动的线程数
    private AtomicInteger activeSize = new AtomicInteger();

    BlockingQueue<Connection> idle;
    BlockingQueue<Connection> busy;

    @Override
    public void init(int initSize, int maxActive, int idleCount, long waitTime) {
        // 省略参数检查
        this.initSize = initSize;
        this.maxActive = maxActive;
        this.idleCount = idleCount;
        this.waitTime = waitTime;

        idle = new LinkedBlockingDeque<>();
        busy = new LinkedBlockingDeque<>();

        initConnection(this.initSize);
    }

    /**
     * 初始化数据库连接
     *
     * @param initSize
     */
    private void initConnection(int initSize) {
        for (int i = 0; i < initSize; i++) {
            if (activeSize.get() < maxActive){
                // 双重检查
                if (activeSize.incrementAndGet() <= maxActive){
                    try {
                        Connection conn = jdbcUtil.getConnection();
                        idle.offer(conn);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else {
                    activeSize.decrementAndGet();
                }
            }
        }
    }

    @Override
    public Connection get() {
        long start = System.currentTimeMillis();
        // 1. 从空闲组获取
        Connection conn = idle.poll();
        if (conn != null){
            System.out.println("从空闲组获得连接");
            busy.offer(conn);
            return conn;
        }

        // 2. 空闲组没有，没有限制
        if (activeSize.get() < maxActive){
            if (activeSize.incrementAndGet() <= maxActive){
                System.out.println("创建一个新的连接");
                try{
                    conn = jdbcUtil.getConnection();
                    busy.offer(conn);
                    return conn;
                }catch (Exception e){
                    System.out.println("获取连接异常：" + e);
                }
            }else {
                activeSize.decrementAndGet();
            }
        }

        // 3.连接全忙，等待连接归还
        long timeout = waitTime - (System.currentTimeMillis() - start);
        try {
            conn = idle.poll(timeout, TimeUnit.MILLISECONDS);
            if (conn == null){
                throw new TimeoutException("等待获取连接超时，等待时间为：" + timeout + "ms");
            }
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
            Thread.interrupted();
        }
        return conn;
    }

    @Override
    public void recycle(Connection conn) {
        if (conn == null){
            return;
        }

        // 从busy挪到idle
        boolean removed = busy.remove(conn);
        if (removed){
            // 空闲连接太多，释放掉
            if (idleCount < idle.size()){
                jdbcUtil.close(null,null,conn);
                activeSize.decrementAndGet();
                return;
            }

            boolean offerd = idle.offer(conn);
            // 没有放入成功
            if (!offerd){
                jdbcUtil.close(null,null,conn);
                activeSize.decrementAndGet();
            }
        }else {
            // 不在组中，关闭释放连接，防止连接泄露
            jdbcUtil.close(null,null,conn);
            activeSize.decrementAndGet();
        }
    }
}
