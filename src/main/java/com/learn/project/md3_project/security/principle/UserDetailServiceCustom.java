package com.learn.project.md3_project.security.principle;

import com.learn.project.md3_project.entity.User;
import com.learn.project.md3_project.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailServiceCustom implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //gọi tới DB để chỉ định cách xác thực thông qua username
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usename ko tồn tại"));
        List<SimpleGrantedAuthority> list = user.getRoles().stream().map(
                role-> new SimpleGrantedAuthority(role.getRoleName().name())
        ).toList();
        return UserDetailCustom.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(list)
                .build();
    }
}
