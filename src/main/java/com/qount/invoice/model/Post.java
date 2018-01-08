package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author SharathG
 *
 *         Oct 1, 2017
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Post {

	private String id;

	private String postedBy;

	private String message;

	private String createdDate;

	private String updatedDate;

	private Set<String> postedInCompanies;

	private String entityType;

	private String entityID;

	private List<String> badges;

	private String emoji;

	private List<Entity> entityMetadata;

	private String documentID;

	private String documentName;

	private String metadata;
	
	private boolean isLikedByUser;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id != null ? id.trim() : null;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy != null ? postedBy.trim() : null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType != null ? entityType.trim() : null;
	}

	public String getEntityID() {
		return entityID;
	}

	public List<String> getBadges() {
		if (badges == null) {
			badges = new ArrayList<>();
		}
		return badges;
	}

	public void setBadges(List<String> badges) {
		this.badges = badges;
	}

	public void setEntityID(String entityID) {
		this.entityID = entityID != null ? entityID.trim() : null;
	}

	public Set<String> getPostedInCompanies() {
		if (postedInCompanies == null) {
			postedInCompanies = new HashSet<>();
		}
		return postedInCompanies;
	}

	public void setPostedInCompanies(Set<String> postedInCompanies) {
		this.postedInCompanies = postedInCompanies;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate != null ? createdDate.trim() : null;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate != null ? updatedDate.trim() : null;
	}

	public List<Entity> getEntityMetadata() {
		if (entityMetadata == null) {
			entityMetadata = new ArrayList<>();
		}
		return entityMetadata;
	}

	public void setEntityMetadata(List<Entity> entityMetadata) {
		this.entityMetadata = entityMetadata;
	}

	public static class Entity {
		private String name;

		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			if(value == null){
				value = "";
			}
			return value;
		}


	}



	public String getEmoji() {
		return emoji;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}


	public String getDocumentID() {
		return documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public boolean isLikedByUser() {
		return isLikedByUser;
	}

	public void setLikedByUser(boolean isLikedByUser) {
		this.isLikedByUser = isLikedByUser;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			Post post = (Post) obj;
			return post.getId().equalsIgnoreCase(this.id);
		}
		return false;
	}

}
