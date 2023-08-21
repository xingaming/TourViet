package fpt.aptech.api.service;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Informationbooking;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.TourCreate;
import fpt.aptech.api.respository.BookingRepository;
import fpt.aptech.api.respository.PaymentRepository;
import fpt.aptech.api.respository.TourCreateRepository;
import fpt.aptech.api.respository.UsersRepository;
import fpt.aptech.api.respository.informationbookingRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementBooking implements IManagementBooking {

    private final UsersRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final TourCreateRepository tourCreateRepository;
    private final informationbookingRepository informationBookingRepository;

    @Autowired
    public ManagementBooking(TourCreateRepository tourCreateRepository, UsersRepository userRepository, BookingRepository bookingRepository, PaymentRepository paymentRepository, informationbookingRepository informationBookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.informationBookingRepository = informationBookingRepository;
        this.tourCreateRepository = tourCreateRepository;
    }

    ;

    @Override
    public List<Booking> getAllBooking(int adminId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy danh sách ScheduleImage theo companyId của admin
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId))
                .collect(Collectors.toList());
    }

    @Override
    public Booking getBookingById(int adminId, int bookingId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        Booking booking = bookingRepository.findById(bookingId).get();
        if (!booking.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
        }

        return booking;
    }

    @Override
    public Booking editBookingById(int adminId, Booking booking) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Payment getPaymentByBookingId(int adminId, int bookingId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        Payment payment = paymentRepository.findByBookingId(bookingId);

        if (!payment.getBookingId().getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
        }
        return payment;
    }

    @Override
    public Payment editPaymentById(int adminId, Payment payment) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Booking deleteBookingById(int adminId, int bookingId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        Booking booking = bookingRepository.findById(bookingId).get();
        if (!booking.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
        }

        informationBookingRepository.deleteIn4BookingByBookingId(bookingId);
        paymentRepository.deleteByBookingId(bookingId);

        bookingRepository.deleteById(bookingId);

        return booking;
    }

    @Override
    public Booking deleteBookingByIdUser(int bookingId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Payment adminCompanyId = paymentRepository.findByBookingId(bookingId);

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId.getStatus() != 0) {
            throw new IllegalArgumentException("Paid");
        }

        Booking booking = bookingRepository.findById(bookingId).get();
//        if(!booking.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)){
//            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
//        }

        informationBookingRepository.deleteIn4BookingByBookingId(bookingId);
        paymentRepository.deleteByBookingId(bookingId);

        bookingRepository.deleteById(bookingId);

        return booking;
    }

    @Override
    public TourCreate deleteTourCreate(int bookingId) {
        Optional<TourCreate> optionalBooking = tourCreateRepository.findById(bookingId);

        if (optionalBooking.isPresent()) {
            TourCreate booking = optionalBooking.get();
            tourCreateRepository.deleteById(booking.getId());
            return booking;
        } else {
            // Handle the case when no booking with the given ID is found
            // For example, you can throw an exception or return null
            return null;
        }
    }

    @Override
    public List<Informationbooking> getInformationBooking(int adminId, int bookingId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        Booking booking = bookingRepository.findById(bookingId).get();
        if (!booking.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
        }

        List<Informationbooking> informationBooking = informationBookingRepository.getInformationBookingByBookingId(bookingId);

        return informationBooking;
    }

}
