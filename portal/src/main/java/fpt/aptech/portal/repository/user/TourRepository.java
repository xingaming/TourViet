/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.portal.repository.user;

import fpt.aptech.portal.entities.TourCreate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author NGOC THAI
 */
public interface TourRepository extends JpaRepository<TourCreate, Integer> {
    
}
