package zzw.demo.myspring.core.aop.executor;

import zzw.demo.myspring.core.aop.annotation.Before;
import zzw.demo.myspring.core.aop.annotation.Bound;
import zzw.demo.myspring.core.ioc.annotation.Component;
import zzw.demo.myspring.core.ioc.annotation.Scope;
import zzw.demo.myspring.core.ioc.constant.ScopeConstant;

import java.lang.reflect.InvocationTargetException;

/**
 * 前置通知
 */

@Component
@Scope(ScopeConstant.PROTOTYPE)
@Bound(Before.class)
public class BeforeAdviceExecutor extends AdviceExecutor {

    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        adviceMethod.invoke(adviceObj);
    }

    @Override
    int getOrder() {
        return 1;
    }
}
