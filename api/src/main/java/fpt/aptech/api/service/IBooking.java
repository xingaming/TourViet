package fpt.aptech.api.service;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.MoMoPaymentResponse;
import java.util.Map;


public interface IBooking {
    MoMoPaymentResponse createBooking(Map<String, Object> requestBody, int userId);
    void handleMoMoIPN(MoMoPaymentResponse paymentResponse);
    MoMoPaymentResponse createBookingV2(Map<String, Object> requestBody, int userId);
    Booking createBookingCashPayment(Map<String, Object> requestBody, int userId);
    void bookTour(Booking book);
}
