package zzw.demo.myspring.core.aop.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bound {
    Class<? extends Annotation> value();
}
