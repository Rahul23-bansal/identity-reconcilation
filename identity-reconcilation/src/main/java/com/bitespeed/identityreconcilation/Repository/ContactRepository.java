package com.bitespeed.identityreconcilation.Repository;

import com.bitespeed.identityreconcilation.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByEmail(String email);

    List<Contact> findByPhoneNumber(String phoneNumber);

    List<Contact> findByLinkedId(Long linkedId);

}
