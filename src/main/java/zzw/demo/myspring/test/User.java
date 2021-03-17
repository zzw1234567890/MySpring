package zzw.demo.myspring.test;

import zzw.demo.myspring.core.ioc.annotation.Autowired;
import zzw.demo.myspring.core.ioc.annotation.Component;
import zzw.demo.myspring.core.ioc.annotation.Scope;
import zzw.demo.myspring.core.ioc.constant.ScopeConstant;
import lombok.Data;

@Data
@Component
@Scope(ScopeConstant.PROTOTYPE)
public class User {
    private int id;
    @Autowired
    private UserService userServiceImpl;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userService=" + (userServiceImpl != null) +
                '}';
    }
}
