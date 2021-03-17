package zzw.demo.myspring.core.aop;

import org.junit.Test;
import zzw.demo.myspring.core.AnnotationApplicationContext;
import zzw.demo.myspring.core.ApplicationContext;
import zzw.demo.myspring.test.Config;
import zzw.demo.myspring.test.User;
import zzw.demo.myspring.test.UserService;

public class AopTest {
    /**
     * 测试Aop功能
     */
    @Test
    public void test1(){
        long startTime = System.currentTimeMillis();
        ApplicationContext applicationContext = new AnnotationApplicationContext(Config.class);
        long useTime = System.currentTimeMillis() - startTime;
        System.out.println("MySpring start success, " + useTime + " ms.");
        UserService userService = applicationContext.getBean(UserService.class);
        User user = userService.getUser();
        System.out.println(user.getUserServiceImpl() == userService);
    }
    @Test
    public void test2(){

    }
}
