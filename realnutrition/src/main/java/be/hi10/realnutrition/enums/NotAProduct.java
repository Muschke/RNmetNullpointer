package be.hi10.realnutrition.enums;

// This enum holds the EAN codes for additional items on the invoice which are not products
// It is used to filter out the NotAProduct items so they don't end up in the database
// Because they don't have a stock

public enum NotAProduct {
// Groothandel: gratis verzending boven de 100 euro	
	// TBE
	WHOLESALE_FREE_SHIPPING_GT_100("7424904586560"),

// Retail customers: gratis verzending boven de 50 euro
// TB2C
	RETAIL_FREE_SHIPPING_GT_50("7424904588540"),

// Bio klanten: gratis verzending boven de 50 euro
// TBIO
	BIO_FREE_SHIPPING_GT_50("7424904578534"),

// Optie snelle verzending
// TBEQ
	FAST_SHIPPING("7424904562540"),

// Transportkost Europa
// TEU
	INTERNATIONAL_SHIPPING_EU("7424904581596");

	public final String ean;

	private NotAProduct(String ean) {
		this.ean = ean;
	}
}
