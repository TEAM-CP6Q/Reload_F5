package com.f5.authserver.Service.Communication;

import com.f5.authserver.DTO.User.UserDetailDTO;

import java.net.URISyntaxException;

public interface AccountCommunicationService {
    void registerAccount(UserDetailDTO userDetailDTO);
    void deleteAccount(Long id) throws URISyntaxException;
    String getAccountEmail(Long id) throws URISyntaxException;
    void releaseAccount(Long id) throws URISyntaxException;
}
