/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.api.respository;

import fpt.aptech.api.models.TourCreate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author NGOC THAI
 */
public interface TourCreateRepository extends JpaRepository<TourCreate, Integer> {
    
}
