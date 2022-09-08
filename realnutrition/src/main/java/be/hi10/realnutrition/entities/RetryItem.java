package be.hi10.realnutrition.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "retryitems")

public class RetryItem implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "created_on")
	private LocalDateTime createdOn;

	private String ean;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "last_changed")
	private LocalDateTime lastChanged;

	@Column(name = "projected_stock")
	private Integer projectedStock;

	private String website;

	public RetryItem() {
	}

	public RetryItem(String ean, String itemId, Integer projectedStock, String website) {
		this.ean = ean;
		this.itemId = itemId;
		this.projectedStock = projectedStock;
		this.website = website;
	}

	public RetryItem(String ean, String itemId, String website) {
		this.ean = ean;
		this.itemId = itemId;
		this.website = website;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public String getEan() {
		return this.ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getItemId() {
		return this.itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public LocalDateTime getLastChanged() {
		return this.lastChanged;
	}

	public void setLastChanged(LocalDateTime lastChanged) {
		this.lastChanged = lastChanged;
	}

	public Integer getProjectedStock() {
		return this.projectedStock;
	}

	public void setProjectedStock(Integer projectedStock) {
		this.projectedStock = projectedStock;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((ean == null) ? 0 : ean.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetryItem other = (RetryItem) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (ean == null) {
			if (other.ean != null)
				return false;
		} else if (!ean.equals(other.ean))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (website != other.website)
			return false;
		return true;
	}

}
