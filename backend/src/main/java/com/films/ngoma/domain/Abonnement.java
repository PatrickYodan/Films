package com.films.ngoma.domain;

import com.films.ngoma.enums.StatutAbonnement;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Access(AccessType.FIELD)
@Table(name = Abonnement.TABLE_NAME)
public class Abonnement extends AbstractEntity {

	public static final String TABLE_NAME = "abonnement";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "utilisateur_id")
	private Utilisateur utilisateur;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pass_id")
	private Pass pass;

	@Column(name = "date_debut")
	private LocalDateTime dateDebut;

	@Column(name = "date_fin")
	private LocalDateTime dateFin;

	@Enumerated(EnumType.STRING)
	private StatutAbonnement statut;

	protected Abonnement() {
	}

	/**
	 * Crée un abonnement démarrant à {@code debut} pour la durée du pass.
	 */
	public Abonnement(Utilisateur utilisateur, Pass pass, LocalDateTime debut) {
		this.utilisateur = utilisateur;
		this.pass = pass;
		this.dateDebut = debut;
		this.dateFin = debut.plusHours(pass.getDureeHeures());
		this.statut = StatutAbonnement.ACTIF;
	}

	public boolean estActif(LocalDateTime maintenant) {
		return statut == StatutAbonnement.ACTIF && dateFin.isAfter(maintenant);
	}

	@Override
	public Long getId() {
		return id;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public Pass getPass() {
		return pass;
	}

	public LocalDateTime getDateDebut() {
		return dateDebut;
	}

	public LocalDateTime getDateFin() {
		return dateFin;
	}

	public StatutAbonnement getStatut() {
		return statut;
	}
}
