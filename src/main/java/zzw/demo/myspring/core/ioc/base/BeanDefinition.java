package zzw.demo.myspring.core.ioc.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zzw.demo.myspring.core.ioc.constant.ScopeConstant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanDefinition {
    private Class<?> beanClass;
    private String beanName;
    private ScopeConstant scope;
    private boolean lazy;
}
