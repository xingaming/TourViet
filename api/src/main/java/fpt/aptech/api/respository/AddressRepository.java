package fpt.aptech.api.respository;

import fpt.aptech.api.models.Address;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a FROM Address a WHERE a.regionId.id = :regionId")
    List<Address> getAddressByRegionId(@Param("regionId") int regionId);
}
