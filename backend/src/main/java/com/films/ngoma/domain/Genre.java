package com.films.ngoma.domain;

import com.films.ngoma.enums.JpaConstants;
import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@Table(name = Genre.TABLE_NAME)
public class Genre implements JpaConstants {

	public static final String TABLE_NAME = "genre";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	private String nom;

	protected Genre() {
	}

	public Long getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}
}
