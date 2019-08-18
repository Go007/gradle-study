package com.hong.util;

import com.hong.config.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author wanghong
 * @date 2019/08/18 17:20
 **/
@Component
public class JDBCDao {

    @Autowired
    private ConnectionPool connectionPool;

    @Autowired
    private JDBCUtil jdbcUtil;

    /**
     * 注解@PostConstruct与@PreDestroy
     *
     * PostConstruct 用于在依赖关系注入完成之后需要执行的方法上，以执行任何初始化。
     */
    @PostConstruct
    private void init(){
        connectionPool.init(10,100,10,5000);
    }

    public List<Map<String, Object>> select(String sql) {
        List<Map<String, Object>> returnResultToList = null;
        try {
            // 从连接池中获取连接
            Connection connection = connectionPool.get();
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet resultSet = pst.executeQuery();
            returnResultToList = jdbcUtil.returnResultToList(resultSet);
            // 使用完，将连接放回连接池
            connectionPool.recycle(connection);
        } catch (SQLException e) {
            System.out.println("异常提醒：" + e);
        }
        return returnResultToList;
    }

}
