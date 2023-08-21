package fpt.aptech.api.resource.admin;

import fpt.aptech.api.models.Transport;
import fpt.aptech.api.service.IManagementTransport;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/")
public class ManagementTransportResource {
    @Autowired
    IManagementTransport service;

    @GetMapping("/transports")
    public ResponseEntity<List<Transport>> showCompanyList() {
        try {
            List<Transport> companies = service.getAllTransport();
            return new ResponseEntity<>(companies, HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception and provide an appropriate error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
