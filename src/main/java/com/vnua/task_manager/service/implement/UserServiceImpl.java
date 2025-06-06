package com.vnua.task_manager.service.implement;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vnua.task_manager.constant.PredefinedRole;
import com.vnua.task_manager.dto.request.userReq.UserCreationRequest;
import com.vnua.task_manager.dto.request.userReq.UserUpdateRequest;
import com.vnua.task_manager.dto.response.userRes.UserResponse;
import com.vnua.task_manager.entity.Role;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.mapper.UserMapper;
import com.vnua.task_manager.repository.RoleRepository;
import com.vnua.task_manager.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(request.getRole()).ifPresent(roles::add);

        user.setRoles(roles);

        String username = user.getUsername();
        String folderPath = "FileOfUser/" + username;
        File userFolder = new File(folderPath);

        // Kiểm tra và tạo folder nếu chưa tồn tại
        if (!userFolder.exists()) {
            boolean folderCreated = userFolder.mkdirs();
            if (!folderCreated) {
                throw new AppException(ErrorCode.FOLDER_CREATION_FAILED);
            }
        }

        user.setPathOfUserFolder(folderPath);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String code = context.getAuthentication().getName();

        User user = userRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.code == authentication.name")
    public UserResponse updateUser(String userCode, UserUpdateRequest request) {
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}
