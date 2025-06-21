package com.f5.accountserver.Service.Communication;

import java.net.URISyntaxException;

public interface CommunicationService {
    String getEmail(String name) throws URISyntaxException;
    Long searchInfo(String username) throws URISyntaxException;
}
