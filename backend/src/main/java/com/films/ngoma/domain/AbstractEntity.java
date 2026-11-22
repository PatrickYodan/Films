package com.films.ngoma.domain;

import com.films.ngoma.enums.JpaConstants;
import com.films.ngoma.security.SecurityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

/**
 * Entité auditée : auteur / date de création et de modification, version (verrou optimiste).
 */
@MappedSuperclass
public abstract class AbstractEntity implements JpaConstants {

	@Column(name = "create_by")
	private String createBy;

	@Column(name = "create_at")
	private LocalDateTime createAt;

	@Column(name = "update_by")
	private String updateBy;

	@Column(name = "update_at")
	private LocalDateTime updateAt;

	@Version
	private long version;

	public abstract Long getId();

	@Transient
	public boolean isNew() {
		return getId() == null;
	}

	@PrePersist
	public void beforeInsert() {
		createAt = LocalDateTime.now();
		createBy = SecurityUtils.lireLoginUtilisateurConnecte();
	}

	@PreUpdate
	public void beforeUpdate() {
		updateAt = LocalDateTime.now();
		updateBy = SecurityUtils.lireLoginUtilisateurConnecte();
	}

	public String getCreateBy() {
		return createBy;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public long getVersion() {
		return version;
	}
}
