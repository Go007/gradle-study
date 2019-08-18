package com.hong.service;

import com.hong.util.JDBCDao;
import com.hong.util.JDBCUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author wanghong
 * @date 2019/08/18 12:09
 **/
@Service
public class JdbcService {

    private static final int CONCURRENCY_LEVEL = 200; // 模拟并发请求

    @Autowired
    private JDBCUtil jdbcUtil;

    @Autowired
    private JDBCDao jdbcDao;

    /**
     * 数据源连接压测
     */
    public void benchmarkTest() {
        String sql = "SELECT * FROM t_user WHERE account = 'wanghong'";
        CountDownLatch cdl = new CountDownLatch(CONCURRENCY_LEVEL);
        for (int i = 0; i < CONCURRENCY_LEVEL; i++) {
            // 多线程模拟客户端并发查询请求
            Thread thread = new Thread(() -> {
                try {
                    // 代码在这里等待CountDownLatch计数为0，代表所有线程可以start，再同时抢占运行
                    cdl.await();
                    execute(sql);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + "线程执行出现异常:" + e.getMessage());
                }
            });
            thread.setName("jdbc-thread-" + i);
            thread.start();
            //线程启动后，倒记数器-1，表示又有一个线程就绪了
            cdl.countDown();
        }
    }

    /**
     * 数据库操作
     * @param sql
     */
    public List<Map<String, Object>> execute(String sql){
       // List<Map<String, Object>> list = jdbcUtil.select(sql);
        List<Map<String, Object>> list = jdbcDao.select(sql);
        return list;
    }

}
