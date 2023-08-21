package fpt.aptech.api.resource.admin;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Informationbooking;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.TourCreate;
import fpt.aptech.api.service.IManagementBooking;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/admin")
public class ManagementBookingResource {
    
    @Autowired
    IManagementBooking service;

    @GetMapping("/{adminId}/bookings")
    public List<Booking> getAllBookings(@PathVariable int adminId) {
        return service.getAllBooking(adminId);
    }

    @GetMapping("/{adminId}/booking/{bookingId}")
    public Booking getBookingById(@PathVariable int adminId, @PathVariable int bookingId) {
        return service.getBookingById(adminId, bookingId);
    }

    @PutMapping("/{adminId}/booking/{bookingId}")
    public Booking editBookingById(@PathVariable int adminId, @PathVariable int bookingId, @RequestBody Booking booking) {
        booking.setId(bookingId);
        return service.editBookingById(adminId, booking);
    }
    
    @DeleteMapping("/{adminId}/booking/{bookingId}")
    public Booking deleteBookingById(@PathVariable int adminId, @PathVariable int bookingId) {
        return service.deleteBookingById(adminId, bookingId);
    }
    
    @DeleteMapping("/booking/{bookingId}")
    public Booking deleteBookingByIdUser(@PathVariable int bookingId) {
        return service.deleteBookingByIdUser(bookingId);
    }
    
     @DeleteMapping("/tour_create/{bookingId}")
    public TourCreate deleteTourCreate(@PathVariable int bookingId) {
        return service.deleteTourCreate(bookingId);
    }

    @GetMapping("/{adminId}/booking/{bookingId}/payment")
    public Payment getPaymentByBookingId(@PathVariable int adminId, @PathVariable int bookingId) {
        return service.getPaymentByBookingId(adminId, bookingId);
    }

    @PutMapping("/{adminId}/payment")
    public Payment editPaymentById(@PathVariable int adminId, @RequestBody Payment payment) {
        return service.editPaymentById(adminId, payment);
    }
    
    @GetMapping("/{adminId}/booking/{bookingId}/informationBookings")
    public List<Informationbooking> getInformationBookings(@PathVariable int adminId, @PathVariable int bookingId) {
        return service.getInformationBooking(adminId, bookingId);
    }
}
