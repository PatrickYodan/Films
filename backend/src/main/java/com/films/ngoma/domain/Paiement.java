package com.films.ngoma.domain;

import com.films.ngoma.enums.MoyenPaiement;
import com.films.ngoma.enums.StatutPaiement;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Access(AccessType.FIELD)
@Table(name = Paiement.TABLE_NAME)
public class Paiement extends AbstractEntity {

	public static final String TABLE_NAME = "paiement";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	private String reference;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "utilisateur_id")
	private Utilisateur utilisateur;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pass_id")
	private Pass pass;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "abonnement_id")
	private Abonnement abonnement;

	@Enumerated(EnumType.STRING)
	private MoyenPaiement moyen;

	private String telephone;

	@Column(name = "montant_fcfa")
	private int montantFcfa;

	@Enumerated(EnumType.STRING)
	private StatutPaiement statut;

	@Column(name = "reference_operateur")
	private String referenceOperateur;

	@Column(name = "confirme_le")
	private LocalDateTime confirmeLe;

	protected Paiement() {
	}

	public Paiement(String reference, Utilisateur utilisateur, Pass pass, MoyenPaiement moyen, String telephone) {
		this.reference = reference;
		this.utilisateur = utilisateur;
		this.pass = pass;
		this.moyen = moyen;
		this.telephone = telephone;
		this.montantFcfa = pass.getPrixFcfa();
		this.statut = StatutPaiement.EN_ATTENTE;
	}

	/** Enregistre l'identifiant de la transaction attribué par l'opérateur. */
	public void transmettre(String referenceOperateur) {
		this.referenceOperateur = referenceOperateur;
	}

	public boolean estEnAttente() {
		return statut == StatutPaiement.EN_ATTENTE;
	}

	public void confirmer(String referenceOperateur, Abonnement abonnement) {
		this.statut = StatutPaiement.CONFIRME;
		this.referenceOperateur = referenceOperateur;
		this.abonnement = abonnement;
		this.confirmeLe = LocalDateTime.now();
	}

	public void echouer(String referenceOperateur) {
		this.statut = StatutPaiement.ECHOUE;
		this.referenceOperateur = referenceOperateur;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getReference() {
		return reference;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public Pass getPass() {
		return pass;
	}

	public Abonnement getAbonnement() {
		return abonnement;
	}

	public MoyenPaiement getMoyen() {
		return moyen;
	}

	public String getTelephone() {
		return telephone;
	}

	public int getMontantFcfa() {
		return montantFcfa;
	}

	public StatutPaiement getStatut() {
		return statut;
	}

	public String getReferenceOperateur() {
		return referenceOperateur;
	}

	public LocalDateTime getConfirmeLe() {
		return confirmeLe;
	}
}
