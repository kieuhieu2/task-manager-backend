package com.vnua.task_manager.service.implement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.service.UserService;
import com.vnua.task_manager.utils.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.multipart.MultipartFile;

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
        user.setUsername(request.getUsername());
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
    @Transactional
    public Boolean deleteUser(String userId) {
        userRepository.deleteByCode(userId);
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUserByUserCode(String userCode) {
        return userMapper.toUserResponse(
                userRepository.findByCode(userCode).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @Override
    public String getFullNameByUserCode(String userCode) {
        String fullName = userRepository.findFullNameByUserCode(userCode);
        if (fullName == null) {
            return new AppException(ErrorCode.USER_NOT_EXISTED).getMessage();
        }
        
        return fullName;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getUsersWithPagination(int page, int size) {
        log.info("In method get Users with pagination: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    public UserResponse updateUserAvatar(String userCode, MultipartFile avatarFile) {
        // Find user by userCode
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        try {
            // Ensure user folder exists
            String userFolder = "FileOfUser/" + user.getUsername();
            File folder = new File(userFolder);
            if (!folder.exists()) {
                boolean folderCreated = folder.mkdirs();
                if (!folderCreated) {
                    throw new AppException(ErrorCode.FOLDER_CREATION_FAILED);
                }
            }
            
            // Save avatar file to user folder
            String filePath = FileUtils.saveFileToPath(userFolder, avatarFile);
            
            // Update user's avatar path
            user.setAvatar(filePath);
            
            // Save user
            user = userRepository.save(user);
            
            return userMapper.toUserResponse(user);
        } catch (IOException e) {
            log.error("Failed to update avatar for user with code: {}", userCode, e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public ResponseEntity<Resource> getUserAvatar(String userCode) {
        try {
            // Find user by userCode
            User user = userRepository.findByCode(userCode)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            
            // Check if user has an avatar
            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Get the avatar path
            Path avatarPath = Paths.get(user.getAvatar());
            Resource resource = new UrlResource(avatarPath.toUri());
            
            // Check if the file exists and is readable
            if (resource.exists() && resource.isReadable()) {
                // Determine the content type of the file
                String contentType = Files.probeContentType(avatarPath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + avatarPath.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Failed to load avatar for user with code: {}", userCode, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
