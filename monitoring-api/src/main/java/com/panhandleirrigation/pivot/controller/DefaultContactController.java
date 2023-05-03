package com.panhandleirrigation.pivot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.ContactPost;
import com.panhandleirrigation.pivot.entity.ContactPut;
import com.panhandleirrigation.pivot.service.ContactService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DefaultContactController implements ContactController {

	@Autowired
	ContactService contactService;

	@Override
	public List<Contact> fetchContactsByKey(String customerKey) {
		log.info("List of contacts was requested for customer key " + customerKey);
		return contactService.fetchContactsByKey(customerKey);
	}

	@Override
	public Contact updateContact(ContactPut contactPut) {
		log.info("Contact is being updated for customer key " + contactPut.getCustomerKey());
		return contactService.updateContact(contactPut);
	}

	@Override
	public Contact createContact(ContactPost contactPost) {
		log.info("New Contact being added");
		return contactService.createContact(contactPost);
	}

	@Override
	public void deleteContact(String customerKey, int contactIndex) {
		log.info("Contact " + contactIndex + " is being deleted for customer key " + customerKey);
		contactService.deleteContact(customerKey, contactIndex);
	}

}
