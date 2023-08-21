package fpt.aptech.portal.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

public class StoreWithAnotherObject {

    private BookingRequest bookingRequest;

    @JsonProperty("in4bookingRequest")
    private List<In4BookingRequest> in4BookingRequest;

    public BookingRequest getBookingRequest() {
        return bookingRequest;
    }

    public void setBookingRequest(BookingRequest bookingRequest) {
        this.bookingRequest = bookingRequest;
    }

    public List<In4BookingRequest> getIn4BookingRequest() {
        return in4BookingRequest;
    }

    public void setIn4BookingRequest(List<In4BookingRequest> in4BookingRequest) {
        this.in4BookingRequest = in4BookingRequest;
    }
}
