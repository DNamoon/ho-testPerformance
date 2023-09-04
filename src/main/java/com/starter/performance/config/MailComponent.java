package com.starter.performance.config;

import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailComponent {

    private final JavaMailSender javaMailSender;

    public boolean sendMail(String mail, String subject, StringBuilder text) {

        boolean result = false;

        MimeMessagePreparator msg = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(mail);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(String.valueOf(text), true);
            }
        };

        try {
            javaMailSender.send(msg);
            result = true;

        } catch (Exception exception) {
            log.info("메일 전송 실패 : " + exception.getMessage());
        }

        return result;
    }
}
