/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.portal.Controller.user;

import fpt.aptech.portal.entities.Review;
import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.entities.Tour;
import fpt.aptech.portal.entities.Users;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author admin
 */
@Controller
@RequestMapping("/reviews")
public class ReviewsController {

    private String redirectUrl;

    @RequestMapping("/comment")
    public String page(Model model, @RequestParam String userContent, @RequestParam String idTour, @RequestParam String idd, @RequestParam String rating1, HttpSession session) {
        Users u = (Users) session.getAttribute("user");
        int tourId = Integer.parseInt(idTour);
//        Tour tour = new Tour();
        Tour tour = new Tour();
        tour.setId(tourId);
        Date date = new Date();
        Review review = new Review();
        review.setUserId(u);
        review.setContent(userContent);
        review.setReviewDate(date);
        review.setTourId(tour);
        float ratingStar = Float.parseFloat(rating1);
        review.setRate(ratingStar);
        createReviewsForUser(review, u, u.getId());

        return "redirect:/user/detail/" + idd;
    }

//    @RequestMapping("/login")
//
//    public String login(Model model, HttpSession session, @RequestParam("id") String id) {
////        redirectUrl = "/user/booktour/" + id;
////        model.addAttribute("message", "Please login before using the service. Thank you");
//
//        return "redirect:/user/login?id=" + id;
//    }
    private Review createReviewsForUser(Review review, Users user, @RequestParam("userId") int userId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/users/{userId}/reviews";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(userId) // Chỉ cần truyền userId vào buildAndExpand
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL, the Authorization header, and the user object as the request body
        ResponseEntity<Review> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.POST,
                new HttpEntity<>(review, headers), // Đưa user vào trong HttpEntity
                Review.class // Loại dữ liệu trả về của API

        );
        // Get the Users object from the response body
        Review result = response.getBody();
        return result;
    }
}
