package zzw.demo.myspring.entity;

import zzw.demo.myspring.core.aop.annotation.*;
import zzw.demo.myspring.core.ioc.annotation.Lazy;

@Lazy
@Aspect
public class UserAspect {
    @PointCut(beanName = "zzw.demo.myspring.entity.UserServiceImpl", method = "*")
    public void pointCut(){}

    @Around
    public void around(){
        System.out.println("around");
    }

    @Before
    public void before(){
        System.out.println("beforeAdvice");
    }

    @After
    public void after(){
        System.out.println("afterAdvice");
    }

    @AfterReturning
    public void afterReturning(){
        System.out.println("afterReturningAdvice");
    }

    @AfterThrowing
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }
}
