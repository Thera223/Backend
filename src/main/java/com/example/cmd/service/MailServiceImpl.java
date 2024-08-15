package com.example.cmd.service;
import com.example.cmd.model.Email;
import com.example.cmd.repository.EmailRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class MailServiceImpl implements MailService {

    private EmailRepository emailRepository;

    @Autowired
    public MailServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public Email Creermail(Email email) {
        return emailRepository.save(email);
    }


}
