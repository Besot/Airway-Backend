package org.airway.airwaybackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface EmailService {
    String sendMail(MultipartFile[] fil, String to, String[] cc, String subject, String body);

}
