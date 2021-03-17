package zzw.demo.myspring.core.ioc.annotation;

import zzw.demo.myspring.core.ioc.constant.ScopeConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scope {
    ScopeConstant value();
}
