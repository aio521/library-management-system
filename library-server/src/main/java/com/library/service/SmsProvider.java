package com.library.service;

public interface SmsProvider {
    void send(String phone, String code);
}
