package be.hi10.realnutrition.apis.amazon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import be.hi10.realnutrition.domain.Product;
import be.hi10.realnutrition.entities.AmazonEanAsin;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.amazon.getCatalog.Catalog;
import be.hi10.realnutrition.services.AmazonEanAsinService;

@Component
public class AllProducts {
	AmazonApiGetAllProducts amazonApi = new AmazonApiGetAllProducts();
	NecessaryFunctions aws4SignatureKeyGenerator = new NecessaryFunctions();
	private final AmazonEanAsinService service;

	public AllProducts(AmazonEanAsinService service) {
		this.service = service;
	}
	private final static Logger LOGGER = LoggerFactory.getLogger(AllProducts.class);
	List<Product> productsWithoutEanNL;
	List<Product> productsWithoutEanFR;
	
	public List<Product> getAllProducts(){
		Thread thread1 = new Thread(() -> {productsWithoutEanNL = getAllProductsFromReport("A1805IZSGTT6HS");});//netherlands
		Thread thread2 = new Thread(() -> {productsWithoutEanFR = getAllProductsFromReport("A13V1IB3VIYZZH");});//france
		
		thread1.start();
		thread2.start();

		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		List<Product> productsWithoutEan = new ArrayList<>();
		productsWithoutEan.addAll(productsWithoutEanNL);
		productsWithoutEan.addAll(productsWithoutEanFR);
		return productsWithoutEan;
	}

	public List<Product> getAllProductsFromReportwithEan() {

		List<Product> productsWithoutEan = getAllProducts();
		List<Product> productsWithEan = new ArrayList<>();
		for (Product product : productsWithoutEan) {
			String asin = product.getAsin();
			String marketplace = product.getMarketplace();

			Catalog catalog = new Catalog();
			String ean = null;

			if (service.findByAsin(asin).isPresent()){
				ean = service.findByAsin(asin).get().getEan();
			}else{
				try {
					catalog = amazonApi.getEancode(asin, marketplace);
					ean = catalog.getIdentifiers().get(0).getIdentifiers().get(0).getIdentifier();
					service.save(new AmazonEanAsin(ean, asin));
				} catch (ApiException e) {
					LOGGER.error("AllProducts --> Something went wrong while getting Eancode per product: " + e.getMessage());
				}
			}
			product.setEan(ean);
			productsWithEan.add(product);
		}
		return productsWithEan;
	}

	public List<Product> getAllProductsFromReport(String country) {
		LOGGER.info("AllProducts --> Start getAllProductsFromReport("+country+")");
		/*
		 * countries.add("A13V1IB3VIYZZH");//france
		 * countries.add("A1805IZSGTT6HS");//netherlands
		 * countries.add("A1C3SOZRARQ6R3");//poland
		 * countries.add("A1F83G8C2ARO7P");//greatbritain
		 * countries.add("A1PA6795UKMFR9");//germany
		 * countries.add("A1RKKUPIHCS9HS");//spain
		 * countries.add("A2NODRKZP88ZB9");//sweden
		 * countries.add("APJ6JRA9NG5V4");//italy
		 */

		List<String> amazonListString = null;
		List<Product> amazonList = new ArrayList<>();

			String reportDocumentUrl = "";

			try {
				reportDocumentUrl = amazonApi.getReportDocument(country).getUrl();
			} catch (ApiException e1) {
				LOGGER.error("AllProducts --> Couldn't get reportDocumentUrl for the country " + country + ":"+ e1.getMessage());
			}

			URL amazonReportUrl = null;

			try {
				amazonReportUrl = new URL(reportDocumentUrl);
			} catch (MalformedURLException e) {
				LOGGER.error("AllProducts --> Couldn't not form url out of url string reportDocumentUrl,country:"+ country + ":" + e.getMessage());
			}

			try {
				aws4SignatureKeyGenerator.downloadFile(amazonReportUrl, country);
			} catch (IOException e) {
				LOGGER.error("AllProducts --> Couldn't download file from url, country:" + country + ":" + e.getMessage());
			}

			try (Stream<String> lines = Files.lines(Paths.get(country))) {
				amazonListString = lines.collect(Collectors.toList());
			} catch (IOException e) {
				LOGGER.error("AllProducts --> Couldn't parse file to List<String>, " + e.getMessage());
			}

			for (var x = 1; x < amazonListString.size(); x++) {
				var line = amazonListString.get(x);
				String lijndelen[] = line.split("\\s+");
				var product = new Product(lijndelen[0], lijndelen[1], null, Double.parseDouble(lijndelen[2]),
						Long.valueOf(lijndelen[3]), country);
				amazonList.add(product);
			}
		LOGGER.info("AllProducts --> Done with getAllProductsFromReport("+country+")");
		return amazonList;
	}
	
    @Scheduled(cron = "0 02 02 * * ?", zone = "Europe/Paris")
    public void flushDB() {
        LOGGER.info("AllProducts --> Start wiping out local DB, amount:" + service.findAll().size());
        service.deleteAll();
        LOGGER.info("AllProducts --> Deleted everything from local DB");
        List<Product> products = getAllProducts();
        LOGGER.info("AllProducts --> getAllproducts size: " + products.size());
        //Above list has duplicate asins in it due to being from various marketplaces, solved using equals.asin method of Product
        List<Product> uniqueProducts = products
                .stream()
                .distinct()
                .collect(Collectors.toList());
        LOGGER.info("AllProducts --> uniqueProducts size: " + uniqueProducts.size());
        for (Product p : uniqueProducts) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.error("AllProducts --> Interrupted exception occured at flushDB for loop");
            }
            String ean = null;
            try {
                ean = amazonApi.getEancode(p.getAsin(), p.getMarketplace()).getIdentifiers().get(0).getIdentifiers().get(0).getIdentifier();
            } catch (ApiException e) {
            	LOGGER.error("AllProducts --> An ApiException at flushDB, trying again in 30 seconds");
                try{Thread.sleep(30000);}catch (InterruptedException ignore){}
                flushDB();
            }
            service.save(new AmazonEanAsin(ean, p.getAsin()));
        }
        
		List<AmazonEanAsin> lijst = service.findAll();
		for(AmazonEanAsin a : lijst){
			if (a.getEan() == null || a.getAsin() == null){
				LOGGER.error("AllProducts --> This isn't perse an error, but a null value was found and the database has been wiped and redone. Check amazon db");
				LOGGER.error("AllProducts --> The product was: asin:"+a.getAsin()+", ean: "+a.getEan() + ", id: "+ a.getId());
				LOGGER.info("AllProducts --> activating flushDB method because a null value was found");
				flushDB();
			}
		}
        LOGGER.info("AllProducts --> flushDB method completed");
    }
}
