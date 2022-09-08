package be.hi10.realnutrition.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the items database table.
 * 
 */
@Entity
@Table(name = "items")
@NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i")
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String ean;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "projected_stock")
	private Integer projectedStock;

	private String website;

	public Item() {
	}

	public Item(String ean, String itemId, Integer projectedStock, String website) {
		this.ean = ean;
		this.itemId = itemId;
		this.projectedStock = projectedStock;
		this.website = website;
	}

	public Item(String ean, String itemId, String website) {
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
		Item other = (Item) obj;
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