package com.pinyougou.manager.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.manager.service.ManagerService;
import com.pinyougou.model.TbManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: chendongtao
 * @Date: 2021/5/1 15:09
 * @Description:
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private ManagerService managerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Integer manageId = Integer.valueOf(username);
        TbManager manager = this.managerService.findByManagerId(manageId);

        if (manager != null) {
            //授权信息-一般是查询出来
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            return new User(username, manager.getManagerPwd(), authorities);
        }
        return null;
    }
}
