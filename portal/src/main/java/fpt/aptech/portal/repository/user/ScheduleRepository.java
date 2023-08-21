/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.portal.repository.user;

import fpt.aptech.portal.entities.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author NGOC THAI
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query(value = "SELECT * FROM schedule ORDER BY price ASC", nativeQuery = true)
    List<Schedule> findAllOrderByPriceAsc();
}
