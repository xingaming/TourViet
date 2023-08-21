package fpt.aptech.api.service;

import fpt.aptech.api.TokenUtil.HashUtils;
import fpt.aptech.api.enums.PaymentMethod;
import fpt.aptech.api.enums.PaymentStatus;
import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Informationbooking;
import fpt.aptech.api.models.MoMoPaymentRequest;
import fpt.aptech.api.models.MoMoPaymentResponse;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.Region;
import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.respository.BookingRepository;
import fpt.aptech.api.respository.PaymentRepository;
import fpt.aptech.api.respository.ScheduleRepository;
import fpt.aptech.api.respository.informationbookingRepository;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookingService implements IBooking {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final informationbookingRepository informationBookingRepository;
    private final ScheduleRepository scheduleRepository;
    private static final BigDecimal EXCHANGE_RATE = new BigDecimal("23738.00");

    @Autowired
    public BookingService(BookingRepository bookingRepository, PaymentRepository paymentRepository, informationbookingRepository informationBookingRepository, ScheduleRepository scheduleRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.informationBookingRepository = informationBookingRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public MoMoPaymentResponse createBooking(Map<String, Object> requestBody, int userId) {

        // Trích xuất thông tin Booking từ requestBody
        Map<String, Object> bookingMap = (Map<String, Object>) requestBody.get("bookingRequest");
        Booking bookingRequest = convertMapToBooking(bookingMap);
        bookingRequest.setBookingDate(new Date());

        // Tạo đối tượng Users cho booking
        Users userBooking = new Users();
        userBooking.setId(userId);
        bookingRequest.setUserId(userBooking);

        //Lấy Price để tạo payment
        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId().getId()).get();
        bookingRequest.setScheduleId(schedule);

        // Lưu Booking vào cơ sở dữ liệu
        Booking savedBooking = bookingRepository.save(bookingRequest);

        // Trích xuất thông tin danh sách Informationbooking từ requestBody
        List<Map<String, Object>> in4bookingListMap = (List<Map<String, Object>>) requestBody.get("in4bookingRequest");
        for (Map<String, Object> in4bookingMap : in4bookingListMap) {
            Informationbooking informationbooking = convertMapToInformationbooking(in4bookingMap);
            informationbooking.setBookingId(savedBooking);
            informationBookingRepository.save(informationbooking);
        }

        BigDecimal amountUSD = calculatePaymentAmount(savedBooking);
        BigDecimal amountVND = convertToVND(amountUSD);

        String amountUSDFormatted = formatAmount(amountUSD);
        String amountVNDDFormatted = formatAmountWithoutDecimals(amountVND);
        String amountVNDCleaned = removeCommas(amountVNDDFormatted);

        // Thực hiện các hành động khác sau khi lưu booking
        String endpoint = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
        String partnerCode = "MOMO";
        String accessKey = "F8BBA842ECF85";
        String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
        String orderInfo = "Thanh toán qua MoMo";
//        String amount = String.valueOf(calculatePaymentAmount(savedBooking));
        String amount = String.valueOf(amountVNDCleaned);
        System.out.println("Amount in VND: " + amount);

        String orderId = savedBooking.getId() + "-" + new Date();
//        String returnUrl = "https://example.com/user/payment/callback";
        String returnUrl = "http://localhost:7777/user/payment/callback";

        String notifyUrl = "http://localhost:8888/api/payment/ipn";
        String extraData = "";

        //String requestId = String.valueOf(System.currentTimeMillis());
        String requestId = "MM1540456472575";
        String requestType = "captureMoMoWallet";

        String rawHash = "partnerCode=" + partnerCode
                + "&accessKey=" + accessKey
                + "&requestId=" + requestId
                + "&amount=" + amount
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&returnUrl=" + returnUrl
                + "&notifyUrl=" + notifyUrl
                + "&extraData=" + extraData;

        String signature = null;
        try {
            signature = HashUtils.signSHA256(rawHash, secretKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(BookingService.class.getName()).log(Level.SEVERE, null, ex);
        }

        MoMoPaymentRequest momoPaymentRequest = new MoMoPaymentRequest();
        momoPaymentRequest.setPartnerCode(partnerCode);
        momoPaymentRequest.setAccessKey(accessKey);
        momoPaymentRequest.setRequestId(requestId);
        momoPaymentRequest.setAmount(amount);
        momoPaymentRequest.setOrderId(orderId);
        momoPaymentRequest.setOrderInfo(orderInfo);
        momoPaymentRequest.setReturnUrl(returnUrl);
        momoPaymentRequest.setNotifyUrl(notifyUrl);
        momoPaymentRequest.setExtraData(extraData);
        momoPaymentRequest.setRequestType(requestType);
        momoPaymentRequest.setSignature(signature);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<MoMoPaymentRequest> entity = new HttpEntity<>(momoPaymentRequest, headers);
        ResponseEntity<MoMoPaymentResponse> responseEntity = restTemplate.exchange(endpoint, HttpMethod.POST, entity, MoMoPaymentResponse.class);
        MoMoPaymentResponse paymentResponse = responseEntity.getBody();

        if (paymentResponse != null && paymentResponse.isSuccess()) {
            String payUrl = paymentResponse.getPayUrl();

            Payment payment = new Payment();
            payment.setId(null);
            payment.setDate(new Date());
            payment.setDescription("PAID");
            BigDecimal longAmount = calculatePaymentAmount(savedBooking);
            payment.setPrice(longAmount);
            payment.setStatus(PaymentStatus.PAID.getValue());
            payment.setPaymentMethod(PaymentMethod.MOMO.getValue());
            Users user = new Users();
            user.setId(userId);
            payment.setUserId(user);
            payment.setBookingId(savedBooking);

            paymentRepository.save(payment);

            return paymentResponse;
        } else {

            return paymentResponse;
        }
    }

    @Override
    public MoMoPaymentResponse createBookingV2(Map<String, Object> requestBody, int userId) {

        // Trích xuất thông tin Booking từ requestBody
        Map<String, Object> bookingMap = (Map<String, Object>) requestBody.get("bookingRequest");
        Booking bookingRequest = convertMapToBooking(bookingMap);
        bookingRequest.setBookingDate(new Date());

        // Tạo đối tượng Users cho booking
        Users userBooking = new Users();
        userBooking.setId(userId);
        bookingRequest.setUserId(userBooking);

        //Lấy Price để tạo payment
        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId().getId()).get();
        bookingRequest.setScheduleId(schedule);

        // Lưu Booking vào cơ sở dữ liệu
        Booking savedBooking = bookingRepository.save(bookingRequest);

        // Trích xuất thông tin danh sách Informationbooking từ requestBody
        List<Map<String, Object>> in4bookingListMap = (List<Map<String, Object>>) requestBody.get("in4bookingRequest");
        for (Map<String, Object> in4bookingMap : in4bookingListMap) {
            Informationbooking informationbooking = convertMapToInformationbooking(in4bookingMap);
            informationbooking.setBookingId(savedBooking);
            informationBookingRepository.save(informationbooking);
        }

        // Thực hiện các hành động khác sau khi lưu booking
        String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
        String partnerCode = "MOMOBKUN20180529";
        String accessKey = "klm05TvNBzhg7h7j";
        String secretKey = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";
        String orderInfo = "Thanh toán qua MoMo";
        String amount = String.valueOf(calculatePaymentAmount(savedBooking));
        String orderId = savedBooking.getId() + "-" + new Date();
        String returnUrl = "https://example.com/payment/callback";
        String notifyUrl = "http://localhost:8888/api/payment/ipn";
        String extraData = "";

        //String requestId = String.valueOf(System.currentTimeMillis());
        String requestId = savedBooking.getId() + "-" + new Date();
        String requestType = "payWithATM";

        String rawHash = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + notifyUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerClientId=" + "abc@gmail.com"
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + returnUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = null;
        try {
            signature = HashUtils.signSHA256(rawHash, secretKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(BookingService.class.getName()).log(Level.SEVERE, null, ex);
        }

        MoMoPaymentRequest momoPaymentRequest = new MoMoPaymentRequest();
        momoPaymentRequest.setPartnerCode(partnerCode);
        momoPaymentRequest.setAccessKey(accessKey);
        momoPaymentRequest.setRequestId(requestId);
        momoPaymentRequest.setAmount(amount);
        momoPaymentRequest.setOrderId(orderId);
        momoPaymentRequest.setOrderInfo(orderInfo);
        momoPaymentRequest.setReturnUrl(returnUrl);
        momoPaymentRequest.setNotifyUrl(notifyUrl);
        momoPaymentRequest.setExtraData(extraData);
        momoPaymentRequest.setRequestType(requestType);
        momoPaymentRequest.setSignature(signature);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<MoMoPaymentRequest> entity = new HttpEntity<>(momoPaymentRequest, headers);
        ResponseEntity<MoMoPaymentResponse> responseEntity = restTemplate.exchange(endpoint, HttpMethod.POST, entity, MoMoPaymentResponse.class);
        MoMoPaymentResponse paymentResponse = responseEntity.getBody();

        if (paymentResponse != null && paymentResponse.isSuccess2()) {
            String payUrl = paymentResponse.getPayUrl();

            Payment payment = new Payment();
            payment.setId(null);
            payment.setDate(new Date());
            payment.setDescription("Chưa thanh toán");
            Long longAmount = Long.valueOf(amount);
            payment.setPrice(BigDecimal.valueOf(longAmount));
            payment.setStatus(PaymentStatus.UNPAID.getValue());
            payment.setPaymentMethod(PaymentMethod.MOMO.getValue());
            Users user = new Users();
            user.setId(userId);
            payment.setUserId(user);
            payment.setBookingId(savedBooking);

            paymentRepository.save(payment);

            return paymentResponse;
        } else {

            return paymentResponse;
        }
    }

    @Override
    public Booking createBookingCashPayment(Map<String, Object> requestBody, int userId) {

        // Trích xuất thông tin Booking từ requestBody
        Map<String, Object> bookingMap = (Map<String, Object>) requestBody.get("bookingRequest");
        Booking bookingRequest = convertMapToBooking(bookingMap);
        bookingRequest.setBookingDate(new Date());

        // Tạo đối tượng Users cho booking
        Users userBooking = new Users();
        userBooking.setId(userId);
        bookingRequest.setUserId(userBooking);

        //Lấy Price để tạo payment
        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId().getId()).get();
        bookingRequest.setScheduleId(schedule);

        // Lưu Booking vào cơ sở dữ liệu
        Booking savedBooking = bookingRepository.save(bookingRequest);

        // Trích xuất thông tin danh sách Informationbooking từ requestBody
        List<Map<String, Object>> in4bookingListMap = (List<Map<String, Object>>) requestBody.get("in4bookingRequest");
        for (Map<String, Object> in4bookingMap : in4bookingListMap) {
            Informationbooking informationbooking = convertMapToInformationbooking(in4bookingMap);
            informationbooking.setBookingId(savedBooking);
            informationBookingRepository.save(informationbooking);
        }

        Payment payment = new Payment();
        payment.setId(null);
        payment.setDate(new Date());
        payment.setDescription("UNPAID");
        BigDecimal longAmount = calculatePaymentAmount(savedBooking);
        payment.setPrice(longAmount);
        payment.setStatus(PaymentStatus.UNPAID.getValue());
        payment.setPaymentMethod(PaymentMethod.CASH.getValue());
        Users user = new Users();
        user.setId(userId);
        payment.setUserId(user);
        payment.setBookingId(savedBooking);

        paymentRepository.save(payment);

        return savedBooking;
    }

    private Booking convertMapToBooking(Map<String, Object> bookingMap) {
        // Chuyển đổi thông tin từ Map sang đối tượng Booking
        Booking booking = new Booking();
        booking.setAdult((Integer) bookingMap.get("adult"));
        booking.setBaby((Integer) bookingMap.get("baby"));
        booking.setSenior((Integer) bookingMap.get("senior"));
        booking.setChildren((Integer) bookingMap.get("children"));
        booking.setBookingDate((Date) bookingMap.get("bookingDate"));
        // Thiết lập các thông tin khác cho booking

        // Kiểm tra nếu regionId là số nguyên, sử dụng đối tượng Region có id tương ứng
        if (bookingMap.get("regionId") instanceof Integer) {
            Region region = new Region();
            region.setId((Integer) bookingMap.get("regionId"));
            booking.setRegionId(region);
        } else if (bookingMap.get("regionId") instanceof Map) {
            Map<String, Object> regionIdMap = (Map<String, Object>) bookingMap.get("regionId");
            Region region = new Region();
            region.setId((Integer) regionIdMap.get("id"));
            booking.setRegionId(region);
        }

        // Kiểm tra nếu scheduleId là số nguyên, sử dụng đối tượng Schedule có id tương ứng
        if (bookingMap.get("scheduleId") instanceof Integer) {
            Schedule schedule = new Schedule();
            schedule.setId((Integer) bookingMap.get("scheduleId"));
            booking.setScheduleId(schedule);
        } else if (bookingMap.get("scheduleId") instanceof Map) {
            Map<String, Object> scheduleIdMap = (Map<String, Object>) bookingMap.get("scheduleId");
            Schedule schedule = new Schedule();
            schedule.setId((Integer) scheduleIdMap.get("id"));
            booking.setScheduleId(schedule);
        }

        return booking;
    }

    private Informationbooking convertMapToInformationbooking(Map<String, Object> in4bookingMap) {
        // Chuyển đổi thông tin từ Map sang đối tượng Informationbooking
        Informationbooking informationbooking = new Informationbooking();
        informationbooking.setName((String) in4bookingMap.get("name"));
        informationbooking.setPhone((String) in4bookingMap.get("phone"));
        informationbooking.setEmail((String) in4bookingMap.get("email"));
        informationbooking.setAddress((String) in4bookingMap.get("address"));
        informationbooking.setGender((Boolean) in4bookingMap.get("gender"));
        informationbooking.setCccd((String) in4bookingMap.get("cccd"));

        return informationbooking;
    }

    @Override
    public void handleMoMoIPN(MoMoPaymentResponse paymentResponse) {
        if (paymentResponse != null && paymentResponse.isSuccess()) {
            String orderId = paymentResponse.getOrderId();
            Payment payment = paymentRepository.findByBookingId(Integer.valueOf(orderId.substring(0, 1)));
            if (payment != null) {
                payment.setDescription("Đã thanh toán");
                payment.setStatus(PaymentStatus.PAID.getValue());
                paymentRepository.save(payment);

                Booking booking = payment.getBookingId();
                bookingRepository.save(booking);
            }
        } else {
            // Xử lý lỗi hoặc thanh toán thất bại tại đây
            if (paymentResponse != null) {
                String orderId = paymentResponse.getOrderId();
                Payment payment = paymentRepository.findByBookingId(Integer.valueOf(orderId.substring(0, 1)));
                if (payment != null) {
                    payment.setDescription("thanh toán thất bại");
                    paymentRepository.save(payment);
                }
            }

        }
    }

    private BigDecimal calculatePaymentAmount(Booking booking) {
        BigDecimal adultPrice = booking.getScheduleId().getPrice().multiply(BigDecimal.valueOf(booking.getAdult()));
        BigDecimal childrenPrice = booking.getScheduleId().getPrice().divide(BigDecimal.valueOf(2)).multiply(BigDecimal.valueOf(booking.getChildren()));
        //baby free
        BigDecimal totalPrice = adultPrice.add(childrenPrice);
        return totalPrice;
    }

    private static BigDecimal convertToVND(BigDecimal amountUSD) {
        return amountUSD.multiply(EXCHANGE_RATE);
    }

    private static String formatAmount(BigDecimal amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(amount);
    }

    private static String formatAmountWithoutDecimals(BigDecimal amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(amount);
    }

    private static String removeCommas(String amountWithCommas) {
        return amountWithCommas.replaceAll(",", "");
    }

    @Override
    public void bookTour(Booking book) {
        bookingRepository.save(book);
    }

}
