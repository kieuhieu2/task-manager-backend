package com.vnua.task_manager.controller;

import java.text.ParseException;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.authReq.AuthenticationRequest;
import com.vnua.task_manager.dto.request.authReq.IntrospectRequest;
import com.vnua.task_manager.dto.request.authReq.LogoutRequest;
import com.vnua.task_manager.dto.request.authReq.RefreshRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.vnua.task_manager.dto.response.authRes.AuthenticationResponse;
import com.vnua.task_manager.dto.response.authRes.IntrospectResponse;
import com.vnua.task_manager.service.implement.AuthenticationServiceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationServiceImpl authenticationServiceImpl;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationServiceImpl.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationServiceImpl.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationServiceImpl.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationServiceImpl.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
