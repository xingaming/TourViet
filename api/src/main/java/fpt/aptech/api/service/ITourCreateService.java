/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package fpt.aptech.api.service;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.TourCreate;
import java.util.List;

/**
 *
 * @author Phu
 */
public interface ITourCreateService {
    void save(TourCreate tour);
    List<TourCreate> getAll();
}
