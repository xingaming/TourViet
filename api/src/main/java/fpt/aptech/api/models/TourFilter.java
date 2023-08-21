package fpt.aptech.api.models;


public class TourFilter {
    private String name;
    private Long minPrice;
    private Long maxPrice;
    private Integer discount;
    private Integer transportId;

    public TourFilter() {
    }

    public TourFilter(String name, Long minPrice, Long maxPrice, Integer discount, Integer transportId) {
        this.name = name;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.discount = discount;
        this.transportId = transportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getTransportId() {
        return transportId;
    }

    public void setTransportId(Integer transportId) {
        this.transportId = transportId;
    }
    
    
}
