package be.hi10.realnutrition.pojos.lightspeed;

public class LightspeedShop {
	
	private String webshopName;
	private String API_KEY;	
	private String API_SECRET;
	
	public LightspeedShop(String webshopName, String API_KEY, String API_SECRET) {
		super();
		this.webshopName = webshopName;
		this.API_KEY = API_KEY;
		this.API_SECRET = API_SECRET;
	}

	public String getWebshopName() {
		return webshopName;
	}

	public String getAPI_KEY() {
		return API_KEY;
	}

	public String getAPI_SECRET() {
		return API_SECRET;
	}
	

}
