package fpt.aptech.portal.service.user;

import fpt.aptech.portal.entities.Booking;
import fpt.aptech.portal.entities.Informationbooking;
import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.repository.user.InfoRepository;
import fpt.aptech.portal.repository.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    InfoRepository inforepository;

    private final JavaMailSender javaMailSender;

    public UserService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void saveFeedback(String recipient, int schduleID) {
//        Schedule schedule = new Schedule();
        List<Booking> listBooing = repository.findAll();
        String userFirstName = "";
        String userLastName = "";
        String userAddress = "";
        String userPhone = "";
        String userEmail = "";
        String servicename = "";
        int tourcode = -1;
        Date checkin = new Date();
        Date checkout = new Date();
        int adult = 0;
        int children = 0;
        int baby = 0;
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal price1 = BigDecimal.ZERO;

        BigDecimal pricechildren = BigDecimal.ZERO;
        BigDecimal pricechildren1 = BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalend = BigDecimal.ZERO;

        String formattedTotal = "";
        String formattedPrice = "";
        String formattedPrice1 = "";

        int scheduleID = -1;
        int bookID = -1;
        StringBuilder tableRowsPice = new StringBuilder();
        String thStyle = "border: 1px solid black; padding: 8px;";
        String tdStyle = "border: 1px solid black; padding: 8px;";

        for (Booking list : listBooing) {
            if (list.getScheduleId().getId() == schduleID) {

//                Informationbooking infoId = infoOption.orElse(null);
                bookID = list.getId();
                userFirstName = list.getUserId().getFirstName();
                userLastName = list.getUserId().getLastName();
                userAddress = list.getUserId().getAddress();
                userPhone = list.getUserId().getPhone();
                userEmail = list.getUserId().getEmail();
                servicename = list.getScheduleId().getTourId().getName();
                tourcode = list.getScheduleId().getTourId().getId();
                checkin = list.getScheduleId().getStartDate();
                checkout = list.getScheduleId().getEndDate();
                adult = list.getAdult();
                children = list.getChildren();
                baby = list.getBaby();

                // mới sửa
                BigDecimal adultBD = BigDecimal.valueOf(adult);
                BigDecimal childrenBD = BigDecimal.valueOf(children);

                price = list.getScheduleId().getPrice().multiply(BigDecimal.valueOf(100));
                price1 = list.getScheduleId().getPrice();
                pricechildren = price1.divide(BigDecimal.valueOf(2)).multiply(BigDecimal.valueOf(100));
                pricechildren1 = price1.divide(BigDecimal.valueOf(2));

                total = (adultBD.multiply(price1)).add(childrenBD.multiply(pricechildren1));
                totalend = total.multiply(BigDecimal.valueOf(100));
                formattedTotal = totalend.divide(BigDecimal.valueOf(100)).toString();
                formattedPrice = price.divide(BigDecimal.valueOf(100)).toString();
                formattedPrice1 = pricechildren.divide(BigDecimal.valueOf(100)).toString();

//                tableRowsPice.append("<td style='" + tdStyle + "'>").append(formattedTotal).append("</td>");
//
//                tableRowsPice.append("</tr>");
            }
        }

//        List<Integer> bookIds = Collections.singletonList(bookID);
        List<Informationbooking> infoOption = inforepository.search(bookID);
        StringBuilder tableRows = new StringBuilder();
        for (Informationbooking listinfo : infoOption) {
            tableRows.append("<tr>");
            tableRows.append("<td style='" + tdStyle + "'>").append(listinfo.getName()).append("</td>");
            tableRows.append("<td style='" + tdStyle + "'>").append(listinfo.getAddress()).append("</td>");
            tableRows.append("<td style='" + tdStyle + "'>").append(listinfo.getEmail()).append("</td>");
            tableRows.append("<td style='" + tdStyle + "'>").append(listinfo.getPhone()).append("</td>");
            String genderText;
            Boolean gender = listinfo.getGender();
            if (gender != null) {
                genderText = gender ? "Male" : "Female";
            } else {
                genderText = "Unknown";
            }
            tableRows.append("<td style='" + tdStyle + "'>").append(genderText).append("</td>");
            tableRows.append("<td style='" + tdStyle + "'>").append(listinfo.getCccd()).append("</td>");
            tableRows.append("</tr>");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String userFullName = userFirstName + " " + userLastName;
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

        String priceTitleStyle = "font-weight: bold; color: #C1AC67;";
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
            helper.setSubject("TourViet - Confirm booking, order #" + bookID);

//            String emailContent = "Xin chào, đây là nội dung của email.";
//            helper.setText(emailContent, false); // false để bật chế độ sử dụng text thông thường
            String emailContent = "<html><body style='background-color: #e0ffff;'>"
                    + "<div style='margin-left:20px'>"
                    + "<h1 style='color: #C1AC67;text-align: center;'>ORDER #" + bookID + "</h1>"
                    + "<p>TourViet has received your BUYING REQUEST - "
                    + "ORDER: <span style='font-weight: bold;'>#" + bookID + "</span></p>"
                    + "<p style='color: #C1AC67;font-style: italic;'> TourViet staff will contact you to reconfirm the number of seats within 01 working day.</p>"
                    + "<p style='color: #C1AC67'>Customer information:</p>"
                    + "<ul>"
                    + "<li><span style='font-weight: bold;'>Full name:</span> " + userFullName + "</li>"
                    + "<li><span style='font-weight: bold;'>Address:</span> " + userAddress + "</li>"
                    + "<li><span style='font-weight: bold;'>Phone:</span> " + userPhone + "</li>"
                    + "<li><span style='font-weight: bold;'>Email:</span> " + userEmail + "</li>"
                    + "</ul>"
                    + "<p style='color: #C1AC67'>Information Services:</p>"
                    + "<ul>"
                    + "<li><span style='font-weight: bold;'>Service name:</span> " + servicename + "</li>"
                    + "<li><span style='font-weight: bold;'>Tour code:</span> " + tourcode + "</li>"
                    + "<li><span style='font-weight: bold;'>Check in:</span> " + checkinDate + "</li>"
                    + "<li><span style='font-weight: bold;'>Check out:</span> " + checkoutDate + "</li>"
                    + "<li><span style='font-weight: bold;'>Duration:</span> " + du + "</li>"
                    + "<li><span style='font-weight: bold;'>Adult:</span> " + adult + "</li>"
                    + "<li><span style='font-weight: bold;'>Children:</span> " + children + "</li>"
                    + "<li><span style='font-weight: bold;'>Baby:</span> " + baby + "</li>"
                                        + "<li style=''><a href='http://localhost:7777/user/detail/" + schduleID + "' target='_blank'>Link to Tour Detail</a></li>"                    
                                        
                    + "</ul>"
                    + "<p style='color: #C1AC67'>Customer Details:</p>"
                    + "<table  style='" + tableStyle + "'>"
                    + "<tr>"
                    + "<th style='" + thStyle + "'>Name</th>"
                    + "<th style='" + thStyle + "'>Address</th>"
                    + "<th style='" + thStyle + "'>Email</th>"
                    + "<th style='" + thStyle + "'>Phone</th>"
                    + "<th style='" + thStyle + "'>Gender</th>"
                    + "<th style='" + thStyle + "'>CCCD</th>"
                    + "</tr>"
                    + tableRows.toString() // Add the table rows here
                    + "</table>"
                    + "<p style='color: #C1AC67'>Price List:</p>"
                    + "<p style='" + priceValueStyle + "'>Price for adults: " + formattedPrice + " USD</p>"
                    + "<p style='" + priceValueStyle + "'>Price for children: " + formattedPrice1 + " USD</p>"
                    + "<table  style='" + tableStyle + "'>"
                    + "<tr>"
                    + "<th style='" + thStyle + "'>Number of adults</th>"
                    + "<th style='" + thStyle + "'>Number of children</th>"
                    + "<th style='" + thStyle + "'>Number of children</th>"
                    + "<th style='" + thStyle + "'>Total</th>"
                    + "</tr>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + adult + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + children + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + baby + "</td>"
                    + "<td style='border: 1px solid black; padding: 8px;'>" + formattedTotal + additionalText + "</td>"
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

    public List<Booking> getAllBooking() {
        return repository.findAll();
    }
    
    public List<Informationbooking> getInfo(int id) {
        return inforepository.search(id);
    }

}
