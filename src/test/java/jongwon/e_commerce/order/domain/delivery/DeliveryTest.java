package jongwon.e_commerce.order.domain.delivery;

import jongwon.e_commerce.order.exception.InvalidDeliveryStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    @Test
    public void 배송시작으로_상태변경(){
        //Given
        Delivery delivery = Delivery.createDelivery(1L, "고양시");
        delivery.setDeliveryStatus(DeliveryStatus.READY);

        //When
        delivery.startDelivery("255-01-000");

        //Then
        assertEquals("255-01-000", delivery.getTrackingNo());
        assertEquals(DeliveryStatus.SHIPPED, delivery.getDeliveryStatus());
    }

    @Test
    public void 배송시작으로_상태변경_예외(){
        //Given
        Delivery delivery = Delivery.createDelivery(1L, "고양시");
        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);

        //Then
        assertThrows(InvalidDeliveryStateException.class, () -> delivery.startDelivery("255-05-01"));
    }

    @Test
    public void 배송완료로_상태변경(){
        //Given
        Delivery delivery = Delivery.createDelivery(1L, "고양시");
        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);

        //When
        delivery.completeDelivery();

        //Then
        assertEquals(DeliveryStatus.DELIVERED, delivery.getDeliveryStatus());
    }

    @Test
    public void 배송완료로_상태변경_예외(){
        //Given
        Delivery delivery = Delivery.createDelivery(1L, "고양시");
        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);

        //Then
        assertThrows(InvalidDeliveryStateException.class, () -> delivery.completeDelivery());
    }
}