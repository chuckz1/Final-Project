package com.panhandleirrigation.pivot.service;

import java.util.List;

import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.ContactPost;
import com.panhandleirrigation.pivot.entity.ContactPut;

public interface ContactService {

	List<Contact> fetchContactsByKey(String customerKey);

	Contact updateContact(ContactPut contactPut);

	Contact createContact(ContactPost contactPost);

	void deleteContact(String customerKey, int contactIndex);
}
