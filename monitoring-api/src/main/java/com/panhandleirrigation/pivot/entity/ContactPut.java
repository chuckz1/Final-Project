package com.panhandleirrigation.pivot.entity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class ContactPut {
	@NotNull
	@Length(max = 30)
	@Pattern(regexp = "[\\w\\s]*")
	String customerKey;
	
	@Valid
	@NotNull
	Contact contact;
}
