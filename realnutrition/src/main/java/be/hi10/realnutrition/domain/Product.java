package be.hi10.realnutrition.domain;

import java.util.Objects;

public class Product {
	private String sku;
	private String asin;
	private String ean;
	private double price;
	private long quantity;
	private String marketplace;
	
	
	public Product(String sku, String asin, String ean, double price, long quantity, String marketplace) {
		this.sku = sku;
		this.asin = asin;
		this.ean = ean;
		this.price = price;
		this.quantity = quantity;
		this.marketplace = marketplace;
	}


	public void setSku(String sku) {
		this.sku = sku;
	}


	public void setAsin(String asin) {
		this.asin = asin;
	}


	public void setEan(String ean) {
		this.ean = ean;
	}


	public void setPrice(double price) {
		this.price = price;
	}


	public void setQuanity(long quanity) {
		this.quantity = quanity;
	}
	
	public void setMarketplace(String marketplace) {
		this.marketplace = marketplace;
	}


	public String getSku() {
		return sku;
	}


	public String getAsin() {
		return asin;
	}


	public String getEan() {
		return ean;
	}


	public double getPrice() {
		return price;
	}


	public long getQuanity() {
		return quantity;
	}


	public String getMarketplace() {
		return marketplace;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Product product = (Product) o;
		return asin.equals(product.asin);
	}

	@Override
	public int hashCode() {
		return Objects.hash(asin);
	}
}
