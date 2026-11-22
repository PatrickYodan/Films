package com.films.ngoma.domain;

import com.films.ngoma.enums.Qualite;
import jakarta.persistence.*;

/**
 * Formule d'accès (Pass Jour, Semaine, Mois).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Pass.TABLE_NAME)
public class Pass extends AbstractEntity {

	public static final String TABLE_NAME = "pass";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	private String code;
	private String nom;

	@Column(name = "duree_heures")
	private int dureeHeures;

	@Column(name = "prix_fcfa")
	private int prixFcfa;

	@Column(name = "ecrans_max")
	private int ecransMax;

	@Enumerated(EnumType.STRING)
	@Column(name = "qualite_max")
	private Qualite qualiteMax;

	private String etiquette;
	private String description;
	private boolean actif;

	protected Pass() {
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getNom() {
		return nom;
	}

	public int getDureeHeures() {
		return dureeHeures;
	}

	public int getPrixFcfa() {
		return prixFcfa;
	}

	public int getEcransMax() {
		return ecransMax;
	}

	public Qualite getQualiteMax() {
		return qualiteMax;
	}

	public String getEtiquette() {
		return etiquette;
	}

	public String getDescription() {
		return description;
	}

	public boolean isActif() {
		return actif;
	}
}
