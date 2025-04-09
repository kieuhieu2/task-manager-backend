package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.SecurityService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityServiceImpl implements SecurityService {
    UserRepository userRepository;

    @Override
    public Boolean isGroupLeader(Integer groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String code = authentication.getName();
        User currentUser = userRepository.findByCode(code).
                orElseThrow(() -> new AppException("User not found"));

        return currentUser.getGroupLeaders()
                .stream()
                .anyMatch(group -> group.getGroupId().equals(groupId));
    }

}
