package com.panhandleirrigation.pivot.entity;

import java.math.BigDecimal;
import java.util.Comparator;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pivot implements Comparable<Pivot>{
	private Long pivotPK;
	
	@Length(max = 10)
	@Pattern(regexp = "[\\w\\s]*")
	private String publicKey;
	
	@NotNull
	@Length(max = 30)
	@Pattern(regexp = "[\\w\\s]*")
	private String pivotName;
	
	@NotNull
	private PivotErrorStatus errorStatus;
	
	@NotNull
	@Max(365)
	@Min(0)
	private BigDecimal rotation;
	
	@JsonIgnore
	public Long getPivotPK() {
		return pivotPK;
	}

	@Override
	public int compareTo(Pivot that) {
		return Comparator.comparing(Pivot::getPivotName).compare(this, that);
	}
}