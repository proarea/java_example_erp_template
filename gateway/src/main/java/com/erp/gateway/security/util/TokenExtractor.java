package com.erp.gateway.security.util;

public interface TokenExtractor {

    String extract(String payload);

}
