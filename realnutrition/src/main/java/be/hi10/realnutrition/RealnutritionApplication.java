package be.hi10.realnutrition;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
//this version is live on bitvise as of 02.09.2022
@SpringBootApplication
public class RealnutritionApplication {
	public static void main(String[] args) {
		SpringApplication.run(RealnutritionApplication.class, args);
	}

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
		return server -> {
			if (server instanceof TomcatServletWebServerFactory) {
				((TomcatServletWebServerFactory) server).addAdditionalTomcatConnectors(redirectConnector());
			}
		};
	}

	private Connector redirectConnector() {
		Connector connector = new Connector("AJP/1.3");
		connector.setScheme("http");
		connector.setPort(9090);
		connector.setSecure(false);
		connector.setAllowTrace(false);
		return connector;	
	}
}