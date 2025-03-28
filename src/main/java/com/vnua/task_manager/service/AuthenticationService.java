package com.vnua.task_manager.service;

import com.nimbusds.jose.JOSEException;
import com.vnua.task_manager.dto.request.authReq.AuthenticationRequest;
import com.vnua.task_manager.dto.request.authReq.IntrospectRequest;
import com.vnua.task_manager.dto.request.authReq.LogoutRequest;
import com.vnua.task_manager.dto.request.authReq.RefreshRequest;
import com.vnua.task_manager.dto.response.authRes.AuthenticationResponse;
import com.vnua.task_manager.dto.response.authRes.IntrospectResponse;
import java.text.ParseException;

public interface AuthenticationService {
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    public AuthenticationResponse authenticate(AuthenticationRequest request);
    public void logout(LogoutRequest request) throws ParseException, JOSEException;
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
