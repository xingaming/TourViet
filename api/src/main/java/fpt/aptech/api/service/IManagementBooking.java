package fpt.aptech.api.service;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Informationbooking;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.TourCreate;
import java.util.List;

public interface IManagementBooking {

    List<Booking> getAllBooking(int userId);

    Booking getBookingById(int userId, int bookingId);

    Booking editBookingById(int userId, Booking booking);

    Booking deleteBookingById(int userId, int bookingId);

    TourCreate deleteTourCreate(int bookingId);

    Payment getPaymentByBookingId(int userId, int bookingId);

    Payment editPaymentById(int userId, Payment payment);

    Booking deleteBookingByIdUser(int bookingId);

    List<Informationbooking> getInformationBooking(int userId, int bookingId);

}
