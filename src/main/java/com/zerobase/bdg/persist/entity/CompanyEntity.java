package com.zerobase.bdg.persist.entity;

import com.zerobase.bdg.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@ToString
@NoArgsConstructor
public class CompanyEntity {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_Increment = IDENTITY
	private Long id;

	@Column(unique = true)
	private String ticker;

	private String name;

	public CompanyEntity(Company company){
		this.ticker = company.getTicker();
		this.name = company.getName();
	}

}
