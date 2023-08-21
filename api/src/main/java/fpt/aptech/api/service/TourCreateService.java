/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.api.service;

import fpt.aptech.api.models.TourCreate;
import fpt.aptech.api.respository.CreateTourRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Phu
 */
@Service
public class TourCreateService implements ITourCreateService{
    private final CreateTourRepository repository;
    
    @Autowired
    public TourCreateService (CreateTourRepository createTourRepo){
        this.repository = createTourRepo;
    }

    @Override
    public void save(TourCreate tour) {
        repository.save(tour);
    }

    @Override
    public List<TourCreate> getAll() {
        return repository.findAll();
    }
    
    
}
