/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.portal.service.user;

import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.repository.user.ScheduleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author NGOC THAI
 */
@Service
public class ScheduleService {
    
    @Autowired
    ScheduleRepository repository;
    
    public List<Schedule> filterPrice(){
        return repository.findAllOrderByPriceAsc();
        
    }
}
