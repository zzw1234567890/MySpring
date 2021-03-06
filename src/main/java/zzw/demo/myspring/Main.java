package zzw.demo.myspring;

import zzw.demo.myspring.core.AnnotationApplicationContext;
import zzw.demo.myspring.core.ApplicationContext;
import zzw.demo.myspring.test.Config;
import zzw.demo.myspring.test.User;
import zzw.demo.myspring.test.UserService;

/**
 * 主函数用于测试系统功能，现测试代码已移交至src/test中
 * 在src/test中进行单元测试
 */
public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ApplicationContext applicationContext = new AnnotationApplicationContext(Config.class);
        long useTime = System.currentTimeMillis() - startTime;
        System.out.println("MySpring start success, " + useTime + " ms.");
        System.out.println(((AnnotationApplicationContext) applicationContext).getSingletonObjects().size());

        UserService userService = applicationContext.getBean(UserService.class);
        User user = userService.getUser();
        System.out.println(user.getUserServiceImpl() == userService);
        System.out.println(System.currentTimeMillis() - startTime);
    }
}
