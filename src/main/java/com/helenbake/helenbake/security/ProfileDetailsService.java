package com.helenbake.helenbake.security;



import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProfileDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userDao;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByPhoneNumber(username).get();
        if(user == null){
            throw new UsernameNotFoundException("Invalid ID or password.");
        }
        return new ProfileDetails(user);
    }
}
