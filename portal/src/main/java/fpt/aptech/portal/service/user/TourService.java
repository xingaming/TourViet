/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.portal.service.user;

import fpt.aptech.portal.entities.TourCreate;
import fpt.aptech.portal.repository.user.TourRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author NGOC THAI
 */
@Service
public class TourService {

    @Autowired
    TourRepository tourRepository;
    private final JavaMailSender javaMailSender;

    public TourService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void save(TourCreate tour) {
        tourRepository.save(tour);
    }

    public void saveFeedback(String recipient, int tourID) {
//        Schedule schedule = new Schedule();
        RestTemplate rs = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/made_tours/" + tourID;
        ResponseEntity<TourCreate> responseEntity = rs.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<TourCreate>() {
        }
        );

        TourCreate resultTourCreate = responseEntity.getBody();
//        List<TourCreate> listBooing = tourRepository.findAll();
        String userFirstName = "";
        String userLastName = "";
        String userAddress = "";
        String userPhone = "";
        String userEmail = "";
        String servicename = "";
        int tourcode = -1;
        Date checkin = new Date();
        Date checkout = new Date();

        BigDecimal price = BigDecimal.ZERO;
        BigDecimal price1 = BigDecimal.ZERO;

        BigDecimal pricechildren = BigDecimal.ZERO;
        BigDecimal pricechildren1 = BigDecimal.ZERO;

        String formattedTotal = "";
        String formattedPrice = "";
        String formattedPrice1 = "";

        int scheduleID = -1;
        int bookID = -1;
        StringBuilder tableRowsPice = new StringBuilder();
        String thStyle = "border: 1px solid black; padding: 8px;";
        String tdStyle = "border: 1px solid black; padding: 8px;";

//        for (Booking list : listBooing) {
//            if (list.getScheduleId().getId() == schduleID) {
//                Informationbooking infoId = infoOption.orElse(null);
//                bookID = list.getId();
        String fullname = resultTourCreate.getFullname();
        String address = resultTourCreate.getAddress();
        String phone = resultTourCreate.getPhone();
        String email = resultTourCreate.getEmail();
        String accommodation = resultTourCreate.getAccommodation();
        String tranport = resultTourCreate.getTransport();
        String schedule = resultTourCreate.getDescription();
        String tourname = resultTourCreate.getDestination();
        checkin = resultTourCreate.getStartdate();
        checkout = resultTourCreate.getEnddate();
        String guideText;
        Boolean guide = resultTourCreate.getTourguide();
        if (guide == true) {
            guideText = "Tour guide";
        } else {
            guideText = "No tour guide";
        }

        int adult = resultTourCreate.getAdult();
        int children = resultTourCreate.getChildren();
        int baby = resultTourCreate.getBaby();

        BigDecimal adultBD = BigDecimal.valueOf(adult);
        BigDecimal childrenBD = BigDecimal.valueOf(children);

        BigDecimal price_adult = resultTourCreate.getPrice();
        BigDecimal price_children = price_adult.divide(new BigDecimal("2"));

        BigDecimal total = (adultBD.multiply(price_adult)).add(childrenBD.multiply(price_children));

//                tableRowsPice.append("<td style='" + tdStyle + "'>").append(formattedTotal).append("</td>");
//
//                tableRowsPice.append("</tr>");
//    }
//}
        StringBuilder tableRows = new StringBuilder();
        String[] tasks = schedule.split("-");

        tableRows.append("<tr>");
        tableRows.append("<td style='" + tdStyle + "'>").append(tourname).append("</td>");
        tableRows.append("<td style='" + tdStyle + "'>"); 

        for (String task : tasks) {
            if (!task.trim().isEmpty()) {
                tableRows.append("- ").append(task.trim()).append("<br>");
            }
        }

        tableRows.append("</td>");
        tableRows.append("<td style='" + tdStyle + "'>").append(accommodation).append("</td>");
        tableRows.append("<td style='" + tdStyle + "'>").append(tranport).append("</td>");
        tableRows.append("</tr>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkinDate = dateFormat.format(checkin);
        String checkoutDate = dateFormat.format(checkout);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(checkin);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(checkout);
        long diffMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
        long duration = diffMillis / (24 * 60 * 60 * 1000);
        long duration1 = duration + 1;
        String du = duration1 + " days " + duration + " nights";

        String tableStyle = "border-collapse: collapse; width: 100%;";

//        String priceTitleStyle = "font-weight: bold; color: #C1AC67;";
        String priceValueStyle = "font-weight: bold; color: #FF0000;";
        var additionalText = " USD";
        try {
            String sender = "tourviet5@gmail.com";
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Địa chỉ email của người nhận
            String recipientEmail = recipient;

            // Thiết lập các thông tin cần thiết cho email
            helper.setTo(new String[]{recipientEmail, sender});
            helper.setSubject("TourViet - Confirm booking, order #" + tourID);

//            String emailContent = "Xin chào, đây là nội dung của email.";
//            helper.setText(emailContent, false); // false để bật chế độ sử dụng text thông thường
            String emailContent = "<html><body style='background-color: #e0ffff;'>"
                    + "<div style='margin-left:20px'>"
                    + "<h1 style='color: #C1AC67;text-align: center;'>ORDER #" + tourID + "</h1>"
                    + "<p>We send you the specific tour schedule - "
                    + "ORDER: <span style='font-weight: bold;'>#" + tourID + "</span></p>"
                    + "<p>We will contact you soon to process the payment </p>"
                    //                    + "<p style='color: #C1AC67;font-style: italic;'> TourViet staff will contact you to reconfirm the number of seats within 01 working day.</p>"
                    + "<p style='color: #C1AC67'>Customer information:</p>"
                    + "<ul>"
                    + "<li><span style='font-weight: bold;'>Full name:</span> " + fullname + "</li>"
                    + "<li><span style='font-weight: bold;'>Address:</span> " + address + "</li>"
                    + "<li><span style='font-weight: bold;'>Phone:</span> " + phone + "</li>"
                    + "<li><span style='font-weight: bold;'>Email:</span> " + email + "</li>"
                    + "</ul>"
                    + "<p style='color: #C1AC67'>Information Services:</p>"
                    + "<ul>"
                    + "<li><span style='font-weight: bold;'>Tour name:</span> " + tourname + "</li>"
                    + "<li><span style='font-weight: bold;'>Check in:</span> " + checkinDate + "</li>"
                    + "<li><span style='font-weight: bold;'>Check out:</span> " + checkoutDate + "</li>"
                    + "<li><span style='font-weight: bold;'>Duration:</span> " + du + "</li>"
                    + "<li><span style='font-weight: bold;'>Adult:</span> " + adult + "</li>"
                    + "<li><span style='font-weight: bold;'>Children:</span> " + children + "</li>"
                    + "<li><span style='font-weight: bold;'>Baby:</span> " + baby + "</li>"
                    + "<li><span style='font-weight: bold;'>Tour guide:</span> " + guideText + "</li>"
                    //                    + "<li style=''><a href='http://localhost:7777/user/detail/" + schduleID + "' target='_blank'>Link to Tour Detail</a></li>"
                    + "</ul>"
                    + "<p style='color: #C1AC67'>Schedule Details:</p>"
                    + "<table  style='" + tableStyle + "'>"
                    + "<tr>"
                    + "<th style='" + thStyle + "'>Tour name</th>"
                    + "<th style='" + thStyle + "'>Schedule</th>"
                    + "<th style='" + thStyle + "'>Accommodation</th>"
                    + "<th style='" + thStyle + "'>Tranport</th>"
                    + "</tr>"
                    + tableRows.toString() // Add the table rows here
                    + "</table>"
                    + "<p style='color: #C1AC67'>Price List:</p>"
                    + "<p style='" + priceValueStyle + "'>Price for adults: " + price_adult + " USD</p>"
                    + "<p style='" + priceValueStyle + "'>Price for children: " + price_children + " USD</p>"
                    + "<table  style='" + tableStyle + "'>"
                    + "<tr>"
                    + "<th style='" + thStyle + "'>Number of adults</th>"
                    + "<th style='" + thStyle + "'>Number of children</th>"
                    + "<th style='" + thStyle + "'>Number of baby</th>"
                    + "<th style='" + thStyle + "'>Total</th>"
                    + "</tr>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + adult + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + children + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + baby + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + total + additionalText + "</td>"
                    + "</table>"
                    + "<p style='font-weight: bold; font-style: italic;'>If you need support or have any questions, please contact <span style='color: #C1AC67;'>info tourviet5@gmail.com</span> or call the Hotline 1900 6568</p>"
                    + "<p style='font-weight: bold; font-style: italic;'>TourViet would like to thank you for trusting to use our service.</p>"
                    + "</div>"
                    + "</body></html>";

            helper.setText(emailContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Xử lý ngoại lệ nếu có lỗi khi gửi email
            e.printStackTrace();
        }
    }
    
    public void cancelFeedback(String recipient, int bookingId, String reason) {
//        Schedule schedule = new Schedule();
        RestTemplate rs = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/made_tours/" + bookingId;
        ResponseEntity<TourCreate> responseEntity = rs.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<TourCreate>() {
        }
        );

        TourCreate resultTourCreate = responseEntity.getBody();
//        List<TourCreate> listBooing = tourRepository.findAll();
        String userFirstName = "";
        String userLastName = "";
        String userAddress = "";
        String userPhone = "";
        String userEmail = "";
        String servicename = "";
        int tourcode = -1;
        Date checkin = new Date();
        Date checkout = new Date();

        BigDecimal price = BigDecimal.ZERO;
        BigDecimal price1 = BigDecimal.ZERO;

        BigDecimal pricechildren = BigDecimal.ZERO;
        BigDecimal pricechildren1 = BigDecimal.ZERO;

        String formattedTotal = "";
        String formattedPrice = "";
        String formattedPrice1 = "";

        int scheduleID = -1;
        int bookID = -1;
        StringBuilder tableRowsPice = new StringBuilder();
        String thStyle = "border: 1px solid black; padding: 8px;";
        String tdStyle = "border: 1px solid black; padding: 8px;";

//        for (Booking list : listBooing) {
//            if (list.getScheduleId().getId() == schduleID) {
//                Informationbooking infoId = infoOption.orElse(null);
//                bookID = list.getId();
        String fullname = resultTourCreate.getFullname();
        String address = resultTourCreate.getAddress();
        String phone = resultTourCreate.getPhone();
        String email = resultTourCreate.getEmail();
        String accommodation = resultTourCreate.getAccommodation();
        String tranport = resultTourCreate.getTransport();
        String schedule = resultTourCreate.getDescription();
        String tourname = resultTourCreate.getDestination();
        checkin = resultTourCreate.getStartdate();
        checkout = resultTourCreate.getEnddate();
        String guideText;
        Boolean guide = resultTourCreate.getTourguide();
        if (guide == true) {
            guideText = "Tour guide";
        } else {
            guideText = "No tour guide";
        }

        int adult = resultTourCreate.getAdult();
        int children = resultTourCreate.getChildren();
        int baby = resultTourCreate.getBaby();

        BigDecimal adultBD = BigDecimal.valueOf(adult);
        BigDecimal childrenBD = BigDecimal.valueOf(children);

        BigDecimal price_adult = resultTourCreate.getPrice();
        BigDecimal price_children = price_adult.divide(new BigDecimal("2"));

        BigDecimal total = (adultBD.multiply(price_adult)).add(childrenBD.multiply(price_children));

//                tableRowsPice.append("<td style='" + tdStyle + "'>").append(formattedTotal).append("</td>");
//
//                tableRowsPice.append("</tr>");
//    }
//}
        StringBuilder tableRows = new StringBuilder();
        String[] tasks = schedule.split("-");

        tableRows.append("<tr>");
        tableRows.append("<td style='" + tdStyle + "'>").append(tourname).append("</td>");
        tableRows.append("<td style='" + tdStyle + "'>"); 

        for (String task : tasks) {
            if (!task.trim().isEmpty()) {
                tableRows.append("- ").append(task.trim()).append("<br>");
            }
        }

        tableRows.append("</td>");
        tableRows.append("<td style='" + tdStyle + "'>").append(accommodation).append("</td>");
        tableRows.append("<td style='" + tdStyle + "'>").append(tranport).append("</td>");
        tableRows.append("</tr>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkinDate = dateFormat.format(checkin);
        String checkoutDate = dateFormat.format(checkout);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(checkin);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(checkout);
        long diffMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
        long duration = diffMillis / (24 * 60 * 60 * 1000);
        long duration1 = duration + 1;
        String du = duration1 + " days " + duration + " nights";

        String tableStyle = "border-collapse: collapse; width: 100%;";

//        String priceTitleStyle = "font-weight: bold; color: #C1AC67;";
        String priceValueStyle = "font-weight: bold; color: #FF0000;";
        var additionalText = " USD";
        try {
            String sender = "tourviet5@gmail.com";
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Địa chỉ email của người nhận
            String recipientEmail = recipient;

            // Thiết lập các thông tin cần thiết cho email
            helper.setTo(new String[]{recipientEmail, sender});
            helper.setSubject("TourViet - Confirm cancel booking, order #" + bookingId);

//            String emailContent = "Xin chào, đây là nội dung của email.";
//            helper.setText(emailContent, false); // false để bật chế độ sử dụng text thông thường
            String emailContent = "<html><body style='background-color: #e0ffff;'>"
                    + "<div style='margin-left:20px'>"
                    + "<h1 style='color: #C1AC67;text-align: center;'>ORDER #" + bookingId + "</h1>"
                    + "<p>We send you the specific tour schedule - "
                    + "ORDER: <span style='font-weight: bold;'>#" + bookingId + "</span></p>"
                    + "<p>We will contact you soon to process the payment </p>"
                    //                    + "<p style='color: #C1AC67;font-style: italic;'> TourViet staff will contact you to reconfirm the number of seats within 01 working day.</p>"
                    + "<p style='color: #C1AC67'>Customer information:</p>"
                    + "<ul>"
                    + "<li><span style='font-weight: bold;'>Full name:</span> " + fullname + "</li>"
                    + "<li><span style='font-weight: bold;'>Address:</span> " + address + "</li>"
                    + "<li><span style='font-weight: bold;'>Phone:</span> " + phone + "</li>"
                    + "<li><span style='font-weight: bold;'>Email:</span> " + email + "</li>"
                    + "</ul>"
                    + "<p style='color: #C1AC67'>Information Services:</p>"
                    + "<ul>"
                    + "<li><span style='font-weight: bold;'>Tour name:</span> " + tourname + "</li>"
                    + "<li><span style='font-weight: bold;'>Check in:</span> " + checkinDate + "</li>"
                    + "<li><span style='font-weight: bold;'>Check out:</span> " + checkoutDate + "</li>"
                    + "<li><span style='font-weight: bold;'>Duration:</span> " + du + "</li>"
                    + "<li><span style='font-weight: bold;'>Adult:</span> " + adult + "</li>"
                    + "<li><span style='font-weight: bold;'>Children:</span> " + children + "</li>"
                    + "<li><span style='font-weight: bold;'>Baby:</span> " + baby + "</li>"
                    + "<li><span style='font-weight: bold;'>Tour guide:</span> " + guideText + "</li>"
                    //                    + "<li style=''><a href='http://localhost:7777/user/detail/" + schduleID + "' target='_blank'>Link to Tour Detail</a></li>"
                    + "</ul>"
                    + "<p style='color: #C1AC67'>Schedule Details:</p>"
                    + "<table  style='" + tableStyle + "'>"
                    + "<tr>"
                    + "<th style='" + thStyle + "'>Tour name</th>"
                    + "<th style='" + thStyle + "'>Schedule</th>"
                    + "<th style='" + thStyle + "'>Accommodation</th>"
                    + "<th style='" + thStyle + "'>Tranport</th>"
                    + "</tr>"
                    + tableRows.toString() // Add the table rows here
                    + "</table>"
                    + "<p style='color: #C1AC67'>Price List:</p>"
                    + "<p style='" + priceValueStyle + "'>Price for adults: " + price_adult + " USD</p>"
                    + "<p style='" + priceValueStyle + "'>Price for children: " + price_children + " USD</p>"
                    + "<table  style='" + tableStyle + "'>"
                    + "<tr>"
                    + "<th style='" + thStyle + "'>Number of adults</th>"
                    + "<th style='" + thStyle + "'>Number of children</th>"
                    + "<th style='" + thStyle + "'>Number of baby</th>"
                    + "<th style='" + thStyle + "'>Total</th>"
                    + "</tr>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + adult + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + children + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + baby + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + total + additionalText + "</td>"
                    + "</table>"
                    + "<h1 style='color: red;'>The tour was canceled, with the reason:" + reason + "</h1>"
                    + "<p style='font-weight: bold; font-style: italic;'>If you need support or have any questions, please contact <span style='color: #C1AC67;'>info tourviet5@gmail.com</span> or call the Hotline 1900 6568</p>"
                    + "<p style='font-weight: bold; font-style: italic;'>TourViet would like to thank you for trusting to use our service.</p>"
                    + "</div>"
                    + "</body></html>";

            helper.setText(emailContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Xử lý ngoại lệ nếu có lỗi khi gửi email
            e.printStackTrace();
        }
    }
}
