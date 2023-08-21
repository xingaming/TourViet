/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.portal.repository.user;

import fpt.aptech.portal.entities.Informationbooking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author NGOC THAI
 */
public interface InfoRepository extends JpaRepository<Informationbooking, Integer> {
    @Query(value = "SELECT * FROM informationbooking  WHERE "
            + "booking_id = :booking_id", nativeQuery = true)
    List<Informationbooking> search(int booking_id);
}
