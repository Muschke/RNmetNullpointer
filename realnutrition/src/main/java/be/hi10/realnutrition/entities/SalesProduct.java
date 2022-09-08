package be.hi10.realnutrition.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sales_products")
public class SalesProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String artikelcode;
    private String ean;
    private String nutnummer;
    private String groep;
    private String product;
    private String productbeschrijving;
    private String verpakking;
    private String btw;
    private String extraInfo;
    private BigDecimal gymprijs;
    private BigDecimal gymprijsBigger250;
    private BigDecimal gymprijsBigger500;
    private BigDecimal gymprijsBigger750;
    private BigDecimal gymprijsBigger1000;
    private boolean korting;  
    private int stock;
    
    protected SalesProduct() {};
    
	public SalesProduct(String artikelcode, String ean, String nutnummer, String groep, String product, String productbeschrijving,
			String verpakking, String btw, String extraInfo, BigDecimal gymprijs, BigDecimal gymprijsBigger250,
			BigDecimal gymprijsBigger500, BigDecimal gymprijsBigger750, BigDecimal gymprijsBigger1000, boolean korting) {
		this.artikelcode = artikelcode;
		this.ean = ean;
		this.nutnummer = nutnummer;
		this.groep = groep;
		this.product = product;
		this.productbeschrijving = productbeschrijving;
		this.verpakking = verpakking;
		this.btw = btw;
		this.extraInfo = extraInfo;
		this.gymprijs = gymprijs;
		this.gymprijsBigger250 = gymprijsBigger250;
		this.gymprijsBigger500 = gymprijsBigger500;
		this.gymprijsBigger750 = gymprijsBigger750;
		this.gymprijsBigger1000 = gymprijsBigger1000;
		this.korting = korting;
	
	}
	
	public long getId() {
		return id;
	}
	
	
	public String getArtikelcode() {
		return artikelcode;
	}

	public void setArtikelcode(String artikelcode) {
		this.artikelcode = artikelcode;
	}

	public String getEan() {
		return ean;
	}
	public void setEan(String ean) {
		this.ean = ean;
	}
	public String getNutnummer() {
		return nutnummer;
	}
	public void setNutnummer(String nutnummer) {
		this.nutnummer = nutnummer;
	}
	public String getGroep() {
		return groep;
	}
	public void setGroep(String groep) {
		this.groep = groep;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getProductbeschrijving() {
		return productbeschrijving;
	}
	public void setProductbeschrijving(String productbeschrijving) {
		this.productbeschrijving = productbeschrijving;
	}
	public String getVerpakking() {
		return verpakking;
	}
	public void setVerpakking(String verpakking) {
		this.verpakking = verpakking;
	}
	public String getBtw() {
		return btw;
	}
	public void setBtw(String btw) {
		this.btw = btw;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	public BigDecimal getGymprijs() {
		return gymprijs;
	}
	public void setGymprijs(BigDecimal gymprijs) {
		this.gymprijs = gymprijs;
	}
	public BigDecimal getGymprijsBigger250() {
		return gymprijsBigger250;
	}
	public void setGymprijsBigger250(BigDecimal gymprijsBigger250) {
		this.gymprijsBigger250 = gymprijsBigger250;
	}
	public BigDecimal getGymprijsBigger500() {
		return gymprijsBigger500;
	}
	public void setGymprijsBigger500(BigDecimal gymprijsBigger500) {
		this.gymprijsBigger500 = gymprijsBigger500;
	}
	public BigDecimal getGymprijsBigger750() {
		return gymprijsBigger750;
	}
	public void setGymprijsBigger750(BigDecimal gymprijsBigger750) {
		this.gymprijsBigger750 = gymprijsBigger750;
	}
	public BigDecimal getGymprijsBigger1000() {
		return gymprijsBigger1000;
	}
	public void setGymprijsBigger1000(BigDecimal gymprijsBigger1000) {
		this.gymprijsBigger1000 = gymprijsBigger1000;
	}
	public boolean isKorting() {
		return korting;
	}
	public void setKorting(boolean korting) {
		this.korting = korting;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}

	@Override
	public String toString() {
		return "SalesProduct [id=" + id + ", artikelcode=" + artikelcode + ", ean=" + ean + ", nutnummer=" + nutnummer
				+ ", groep=" + groep + ", product=" + product + ", productbeschrijving=" + productbeschrijving
				+ ", verpakking=" + verpakking + ", btw=" + btw + ", extraInfo=" + extraInfo + ", gymprijs=" + gymprijs
				+ ", gymprijsBigger250=" + gymprijsBigger250 + ", gymprijsBigger500=" + gymprijsBigger500
				+ ", gymprijsBigger750=" + gymprijsBigger750 + ", gymprijsBigger1000=" + gymprijsBigger1000
				+ ", korting=" + korting + ", stock=" + stock + "]";
	}


    
    
    
    
}
