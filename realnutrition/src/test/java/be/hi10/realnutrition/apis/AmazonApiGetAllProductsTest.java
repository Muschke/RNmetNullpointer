package be.hi10.realnutrition.apis;

import static org.assertj.core.api.Assertions.assertThat;

import be.hi10.realnutrition.apis.amazon.AmazonApiGetAllProducts;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.amazon.reports.Report;
import be.hi10.realnutrition.pojos.amazon.toMarkeplace.MarketplacesArray;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.ResponseEntity;

public class AmazonApiGetAllProductsTest{

    AmazonApiGetAllProducts api = new AmazonApiGetAllProducts();
    @BeforeEach
    void beforeEach(){
        //api = new AmazonApiGetAllProducts();
    }

    @Test
    public void getToMarketplace() throws ApiException {
        AmazonApiGetAllProducts api = new AmazonApiGetAllProducts();
        MarketplacesArray array = api.getToMarketplace();
        assertThat(array).isNotNull();
        assertThat(array).isInstanceOf(MarketplacesArray.class);
    }

    @Test
    public void getReportId() throws ApiException {
        AmazonApiGetAllProducts api = new AmazonApiGetAllProducts();
        String FRANCE = "A13V1IB3VIYZZH";
        String reportId =  api.getReportId(FRANCE);
        assertThat(reportId).hasSize(11);
        assertThat(reportId).containsOnlyDigits();
        assertThat(reportId).doesNotContainAnyWhitespaces();
        assertThat(reportId).isNotBlank();
        assertThat(reportId).isNotEmpty();
        assertThat(reportId).isNotNull();
    }

    @Test
    public void getReport() throws ApiException {
        AmazonApiGetAllProducts api = new AmazonApiGetAllProducts();
        String FRANCE = "A13V1IB3VIYZZH";
        String reportIdParam = api.getReportId(FRANCE);
        ResponseEntity<Report> report = api.getReport(reportIdParam, FRANCE);

        assertThat(report.getBody()).isNotNull();
        assertThat(report).isNotNull();
        assertThat(report.getBody()).isInstanceOf(Report.class);
        assertThat(report.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getEancode() throws ApiException {
        AmazonApiGetAllProducts api = new AmazonApiGetAllProducts();
        String NETHERLANDS = "A1805IZSGTT6HS";
        String ASIN = "B0B28PPKX1";
        assertThat(api.getEancode(ASIN, NETHERLANDS)).isNotNull();
    }
}
