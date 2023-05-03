package com.panhandleirrigation.pivot.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panhandleirrigation.pivot.dao.ContactDao;
import com.panhandleirrigation.pivot.dao.CustomerDao;
import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.ContactPost;
import com.panhandleirrigation.pivot.entity.ContactPut;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultContactService implements ContactService {

	@Autowired
	CustomerDao customerDao;

	@Autowired
	ContactDao contactDao;

	@Autowired
	HelperService helperService;

	@Transactional(readOnly = true)
	@Override
	public List<Contact> fetchContactsByKey(String customerKey) {
		log.info("Service Layer: contact list requested for customer key: " + customerKey);

		// @formatter:off
		Long customerPK = customerDao.convertKeyToPK(customerKey)
				.orElseThrow(() -> new NoSuchElementException(
						"Customer with key = " + customerKey + " was not found"));
		// @formatter:on

		List<Contact> list = contactDao.getContactsByCustomerFK(customerPK);

		Collections.sort(list);

		return list;
	}

	@Transactional
	@Override
	public Contact updateContact(ContactPut contactPut) {
		log.info("Service Layer: Contact is being updated for customer key " + contactPut.getCustomerKey());

		Long customerPK = customerDao.convertKeyToPK(contactPut.getCustomerKey())
				.orElseThrow(() -> new NoSuchElementException(
						"Customer with key = " + contactPut.getCustomerKey() + " was not found"));

		Long contactPK = contactDao.convertKeyToPK(contactPut.getContact().getPublicKey())
				.orElseThrow(() -> new NoSuchElementException(
						"Contact with key = " + contactPut.getContact().getPublicKey() + " was not found"));

		// get the contact to change
		Contact target = contactDao.getContactByCustomerFKandPK(customerPK, contactPK)
				.orElseThrow(() -> new NoSuchElementException(
						"Contact with key = " + contactPut.getContact().getPublicKey() + " was not found"));

		// modify the target
		target.setDescription(contactPut.getContact().getDescription());
		target.setEmail(contactPut.getContact().getEmail());

		// save changes
		return contactDao.updateContact(target).orElseThrow(() -> new RuntimeException(
				"Contact for customer key " + contactPut.getCustomerKey() + " could not be updated"));
	}

	@Transactional
	@Override
	public Contact createContact(ContactPost contactPost) {

		// get the customer primary key
		Long customerPK = customerDao.convertKeyToPK(contactPost.getCustomerKey())
				.orElseThrow(() -> new NoSuchElementException(
						"Customer with key = " + contactPost.getCustomerKey() + " was not found"));

		// get public key
		if (contactPost.getContact().getPublicKey() == null) {
			log.info("Changing public key");
			contactPost.getContact().setPublicKey(helperService.generateKey("contacts", "contact_key"));
		}

		// modify new contact
		Contact newContact = contactPost.getContact();
		newContact.setCustomerFK(customerPK);

		// save the new contact
		return contactDao.createContact(newContact).orElseThrow(() -> new RuntimeException(
				"Contact for customer key " + contactPost.getCustomerKey() + " could not be created"));
	}

	@Transactional
	@Override
	public void deleteContact(String customerKey, String contactKey) {
		Long customerPK = customerDao.convertKeyToPK(customerKey)
				.orElseThrow(() -> new NoSuchElementException("Customer with key = " + customerKey + " was not found"));

		Long contactPK = contactDao.convertKeyToPK(contactKey)
				.orElseThrow(() -> new NoSuchElementException("Contact with key = " + contactKey + " was not found"));

		int count = contactDao.deleteContact(customerPK, contactPK);

		if (count < 1) {
			throw new DataIntegrityViolationException(
					"Contact for customer key " + customerKey + " could not be deleted");
		}
	}

}
