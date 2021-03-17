package zzw.demo.myspring.core.aop.executor;

import zzw.demo.myspring.core.aop.annotation.AfterReturning;
import zzw.demo.myspring.core.aop.annotation.Bound;
import zzw.demo.myspring.core.ioc.annotation.Component;
import zzw.demo.myspring.core.ioc.annotation.Scope;
import zzw.demo.myspring.core.ioc.constant.ScopeConstant;

import java.lang.reflect.InvocationTargetException;

@Component
@Scope(ScopeConstant.PROTOTYPE)
@Bound(AfterReturning.class)
public class AfterReturningAdviceExecutor extends AdviceExecutor {
    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        adviceMethod.invoke(adviceObj);
    }

    @Override
    int getOrder() {
        return 3;
    }
}
