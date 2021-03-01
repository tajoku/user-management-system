package com.interview.usermanagementsystem.integrations;

import com.interview.usermanagementsystem.service.EmailService;
import com.sendgrid.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class SendGridEmailService implements EmailService {

    private static final String NO_RESPONSE = "No response from email service";

    @Autowired
    private SendGrid sendGridClient;

    @Override
    public void sendText(String from, String to, String subject, String body) {
        Response response = sendEmail(from, to, subject, new Content("text/plain", body));
        if (response == null) {
            throw new IllegalStateException(NO_RESPONSE);
        }
        logResponse(response.getStatusCode(), response.getBody(), response.getHeaders());
    }

    private Response sendEmail(String from, String to, String subject, Content content) {
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        mail.setReplyTo(new Email("info@silvergrandeur.com"));
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = sendGridClient.api(request);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return response;
    }

    private void logResponse(int statusCode, String body, Map<String, String> headers) {
        log.debug("Status Code: " + statusCode + ", Body: " + body + ", Headers: " + headers);
    }
}