package jongwon.e_commerce.payment.application;

public interface StockChanger {
    void decreaseStock(String orderId);
    void increaseStock(String orderId);
}
