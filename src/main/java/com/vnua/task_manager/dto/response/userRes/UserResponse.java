package com.vnua.task_manager.dto.response.userRes;

import java.time.LocalDate;
import java.util.Set;

import com.vnua.task_manager.dto.response.authRes.RoleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String userId;
    String username;
    String firstName;
    String lastName;
    String code;
    LocalDate dob;
    Set<RoleResponse> roles;
}
