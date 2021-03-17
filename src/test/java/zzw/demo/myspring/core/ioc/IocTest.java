package zzw.demo.myspring.core.ioc;

import org.junit.Test;
import zzw.demo.myspring.core.AnnotationApplicationContext;
import zzw.demo.myspring.core.ApplicationContext;
import zzw.demo.myspring.test.Config;

public class IocTest {
    /**
     * 测试IOC创建耗时
     */
    @Test
    public void test1(){
        long startTime = System.currentTimeMillis();
        ApplicationContext applicationContext = new AnnotationApplicationContext(Config.class);
        long useTime = System.currentTimeMillis() - startTime;
        System.out.println("MySpring start success, " + useTime + " ms.");
    }
}
