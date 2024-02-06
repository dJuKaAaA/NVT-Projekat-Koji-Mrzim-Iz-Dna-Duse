package nvt.project.smart_home.main.websecurity;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email '%s' not found!".formatted(username)));
        return new UserDetailsImpl(user);
    }
}
