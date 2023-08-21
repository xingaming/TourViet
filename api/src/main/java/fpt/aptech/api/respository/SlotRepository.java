package fpt.aptech.api.respository;

import fpt.aptech.api.models.Slot;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SlotRepository extends JpaRepository<Slot, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Slot s WHERE s.tourId.id = :tourId")
    void deleteSlotsByTour(@Param("tourId") int tourId);

}
