package fpt.aptech.api.service;

import fpt.aptech.api.models.Tour;
import fpt.aptech.api.models.TourCreate;
//import fpt.aptech.api.models.Tourcreate;
import java.util.List;

public interface IManagementToursForHQAndArea {

    List<Tour> viewTours(int adminId);

    List<TourCreate> viewMadeTours();

    TourCreate viewMadeToursById(int tourId);

    Tour viewDetailTour(int adminId, int tourId);

    Tour createTour(int adminId, Tour tour);

    Tour modifyTour(int adminId, int id, Tour tour);

    Tour removeTour(int adminId, int id);
}
