package be.hi10.realnutrition.entities;
import javax.persistence.*;

@Entity
@Table(name = "amazon_ean_asin")
public class AmazonEanAsin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ean;
    private String asin;

    public AmazonEanAsin(String ean, String asin) {
        this.ean = ean;
        this.asin = asin;
    }
    protected AmazonEanAsin(){}

    public Long getId() {
        return id;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    @Override
    public String toString() {
        return "AmazonEanAsin{" +
                "id=" + id +
                ", ean='" + ean + '\'' +
                ", asin='" + asin + '\'' +
                '}';
    }
}
