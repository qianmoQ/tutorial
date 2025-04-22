package org.devlive.tutorial.multithreading.chapter07;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用ThreadLocal实现用户上下文传递
 */
public class UserContextPropagationDemo
{

    /**
     * 主方法，模拟多个用户并发请求
     */
    public static void main(String[] args)
    {
        // 创建请求处理器
        RequestHandler requestHandler = new RequestHandler();

        // 创建线程池，模拟Web服务器的请求处理线程
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 提交多个任务，模拟不同用户的请求
        executorService.execute(() -> {
            requestHandler.handleRequest("1001", "张三", "管理员");
        });

        executorService.execute(() -> {
            requestHandler.handleRequest("1002", "李四", "普通用户");
        });

        executorService.execute(() -> {
            requestHandler.handleRequest("1003", "王五", "访客");
        });

        // 关闭线程池
        executorService.shutdown();
    }

    /**
     * 用户上下文类，包含用户的基本信息
     */
    static class UserContext
    {
        private final String userId;
        private final String username;
        private final String userRole;
        private final String sessionId;

        public UserContext(String userId, String username, String userRole)
        {
            this.userId = userId;
            this.username = username;
            this.userRole = userRole;
            this.sessionId = UUID.randomUUID().toString();
        }

        public String getUserId()
        {
            return userId;
        }

        public String getUsername()
        {
            return username;
        }

        public String getUserRole()
        {
            return userRole;
        }

        public String getSessionId()
        {
            return sessionId;
        }

        @Override
        public String toString()
        {
            return "UserContext{" +
                    "userId='" + userId + '\'' +
                    ", username='" + username + '\'' +
                    ", userRole='" + userRole + '\'' +
                    ", sessionId='" + sessionId + '\'' +
                    '}';
        }
    }

    /**
     * 用户上下文管理器，负责存储和获取用户上下文
     */
    static class UserContextHolder
    {
        // 使用ThreadLocal存储用户上下文
        private static final ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();

        /**
         * 获取用户上下文
         */
        public static UserContext getUserContext()
        {
            UserContext userContext = userContextThreadLocal.get();
            if (userContext == null) {
                throw new IllegalStateException("用户上下文未设置");
            }
            return userContext;
        }

        /**
         * 设置用户上下文
         */
        public static void setUserContext(UserContext userContext)
        {
            userContextThreadLocal.set(userContext);
        }

        /**
         * 清除用户上下文
         */
        public static void clearUserContext()
        {
            userContextThreadLocal.remove();
        }
    }

    /**
     * 模拟过滤器，用于在请求开始时设置用户上下文，在请求结束时清除用户上下文
     */
    static class UserContextFilter
    {
        /**
         * 处理请求前的操作
         */
        public static void preHandle(String userId, String username, String userRole)
        {
            // 创建用户上下文
            UserContext userContext = new UserContext(userId, username, userRole);
            // 设置到ThreadLocal
            UserContextHolder.setUserContext(userContext);
            System.out.println(Thread.currentThread().getName() +
                    " - Filter: 设置用户上下文: " + userContext);
        }

        /**
         * 处理请求后的操作
         */
        public static void postHandle()
        {
            System.out.println(Thread.currentThread().getName() +
                    " - Filter: 清除用户上下文");
            // 清除ThreadLocal
            UserContextHolder.clearUserContext();
        }
    }

    /**
     * 模拟业务服务
     */
    static class BusinessService
    {
        /**
         * 处理业务逻辑
         */
        public void processBusinessLogic()
        {
            // 从ThreadLocal获取用户上下文
            UserContext userContext = UserContextHolder.getUserContext();
            System.out.println(Thread.currentThread().getName() +
                    " - Service: 处理用户 " + userContext.getUsername() + " 的业务");

            // 模拟调用其他服务
            callOtherService();
        }

        /**
         * 调用其他服务
         */
        private void callOtherService()
        {
            // 从ThreadLocal获取用户上下文
            UserContext userContext = UserContextHolder.getUserContext();
            System.out.println(Thread.currentThread().getName() +
                    " - OtherService: 用户角色是 " + userContext.getUserRole());

            // 如果是管理员，执行一些特殊操作
            if ("管理员".equals(userContext.getUserRole())) {
                System.out.println(Thread.currentThread().getName() +
                        " - OtherService: 执行管理员特有操作");
            }
        }
    }

    /**
     * 模拟日志记录器
     */
    static class AuditLogger
    {
        /**
         * 记录审计日志
         */
        public static void logAction(String action)
        {
            // 从ThreadLocal获取用户上下文
            UserContext userContext = UserContextHolder.getUserContext();
            System.out.println(Thread.currentThread().getName() +
                    " - 审计日志: 用户 " + userContext.getUsername() +
                    " (ID: " + userContext.getUserId() + ") 执行了 " + action +
                    ", 会话ID: " + userContext.getSessionId());
        }
    }

    /**
     * 模拟请求处理
     */
    static class RequestHandler
    {
        private final BusinessService businessService = new BusinessService();

        /**
         * 处理请求
         */
        public void handleRequest(String userId, String username, String userRole)
        {
            try {
                // 前置处理：设置用户上下文
                UserContextFilter.preHandle(userId, username, userRole);

                // 记录开始操作
                AuditLogger.logAction("开始请求");

                // 执行业务逻辑
                businessService.processBusinessLogic();

                // 记录结束操作
                AuditLogger.logAction("完成请求");
            }
            finally {
                // 后置处理：清除用户上下文
                UserContextFilter.postHandle();
            }
        }
    }
}
