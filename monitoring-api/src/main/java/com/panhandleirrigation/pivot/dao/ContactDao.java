package com.panhandleirrigation.pivot.dao;

import java.util.List;
import java.util.Optional;

import com.panhandleirrigation.pivot.entity.Contact;

public interface ContactDao {

	List<Contact> getContactsByCustomerFK(Long customerPK);
	
	Optional<Contact> getContactByCustomerFKandIndex(Long customerPK, int contactIndex);

	Optional<Contact> updateContact(Contact target);
	
	Optional<Contact> getContactByPK(Long key);

	Optional<Contact> createContact(Contact contact);

	int deleteContact(Long customerPK, int contactIndex);

	
}
