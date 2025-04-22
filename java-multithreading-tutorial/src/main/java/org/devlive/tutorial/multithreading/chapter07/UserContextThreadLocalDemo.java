package org.devlive.tutorial.multithreading.chapter07;

/**
 * 模拟Web服务器中使用ThreadLocal管理用户上下文
 */
public class UserContextThreadLocalDemo
{

    // 模拟Web服务器的多线程请求处理
    public static void main(String[] args)
    {
        // 模拟三个不同用户的并发请求
        Thread thread1 = new Thread(() -> {
            new UserController().handleRequest("1001", "张三", "管理员");
        }, "用户请求线程1");

        Thread thread2 = new Thread(() -> {
            new UserController().handleRequest("1002", "李四", "普通用户");
        }, "用户请求线程2");

        Thread thread3 = new Thread(() -> {
            new UserController().handleRequest("1003", "王五", "访客");
        }, "用户请求线程3");

        // 启动线程
        thread1.start();
        thread2.start();
        thread3.start();
    }

    // 用户上下文类
    static class UserContext
    {
        private final String userId;
        private final String username;
        private final String userRole;

        public UserContext(String userId, String username, String userRole)
        {
            this.userId = userId;
            this.username = username;
            this.userRole = userRole;
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

        @Override
        public String toString()
        {
            return "UserContext{" +
                    "userId='" + userId + '\'' +
                    ", username='" + username + '\'' +
                    ", userRole='" + userRole + '\'' +
                    '}';
        }
    }

    // 用户上下文管理器
    static class UserContextHolder
    {
        // 使用ThreadLocal存储用户上下文
        private static final ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();

        // 获取用户上下文
        public static UserContext getUserContext()
        {
            return userContextThreadLocal.get();
        }

        // 设置用户上下文
        public static void setUserContext(UserContext userContext)
        {
            userContextThreadLocal.set(userContext);
        }

        // 清除用户上下文
        public static void clearUserContext()
        {
            userContextThreadLocal.remove();
        }
    }

    // 模拟Controller层
    static class UserController
    {
        public void handleRequest(String userId, String username, String userRole)
        {
            // 创建用户上下文并保存到ThreadLocal
            UserContext userContext = new UserContext(userId, username, userRole);
            UserContextHolder.setUserContext(userContext);

            System.out.println(Thread.currentThread().getName() + " - Controller: 处理用户请求，设置上下文");

            // 调用Service层
            new UserService().processUserRequest();
        }
    }

    // 模拟Service层
    static class UserService
    {
        public void processUserRequest()
        {
            // 从ThreadLocal获取用户上下文
            UserContext userContext = UserContextHolder.getUserContext();
            System.out.println(Thread.currentThread().getName() + " - Service: 获取到用户上下文: " + userContext);

            // 调用DAO层
            new UserDao().saveUserData();
        }
    }

    // 模拟DAO层
    static class UserDao
    {
        public void saveUserData()
        {
            // 从ThreadLocal获取用户上下文
            UserContext userContext = UserContextHolder.getUserContext();
            System.out.println(Thread.currentThread().getName() + " - DAO: 获取到用户上下文: " + userContext);
            System.out.println(Thread.currentThread().getName() + " - DAO: 保存用户数据，用户ID: " + userContext.getUserId());

            // 完成请求处理后，清除用户上下文
            UserContextHolder.clearUserContext();
            System.out.println(Thread.currentThread().getName() + " - DAO: 请求处理完成，已清除用户上下文");
        }
    }
}
