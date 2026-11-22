package com.films.ngoma.domain;

import com.films.ngoma.enums.Role;
import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@Table(name = Utilisateur.TABLE_NAME)
public class Utilisateur extends AbstractEntity {

	public static final String TABLE_NAME = "utilisateur";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	private String nom;
	private String email;
	private String telephone;

	@Column(name = "mot_de_passe_hash")
	private String motDePasseHash;

	@Enumerated(EnumType.STRING)
	private Role role;

	protected Utilisateur() {
	}

	public Utilisateur(String nom, String email, String telephone, String motDePasseHash) {
		this.nom = nom;
		this.email = email.trim().toLowerCase();
		this.telephone = telephone;
		this.motDePasseHash = motDePasseHash;
		this.role = Role.ABONNE;
	}

	/**
	 * Met à jour les informations modifiables par l'utilisateur lui-même.
	 */
	public void modifierProfil(String nom, String telephone) {
		this.nom = nom;
		this.telephone = telephone;
	}

	public void changerMotDePasse(String motDePasseHash) {
		this.motDePasseHash = motDePasseHash;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public String getEmail() {
		return email;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getMotDePasseHash() {
		return motDePasseHash;
	}

	public Role getRole() {
		return role;
	}
}
