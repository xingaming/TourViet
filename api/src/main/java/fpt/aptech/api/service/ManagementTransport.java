package fpt.aptech.api.service;

import fpt.aptech.api.models.Transport;
import fpt.aptech.api.respository.TransportRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ManagementTransport implements IManagementTransport{
    
    @Autowired
    TransportRepository transportRepository;

    @Override
    public List<Transport> getAllTransport() {
        return transportRepository.findAll();
    }
    
}
