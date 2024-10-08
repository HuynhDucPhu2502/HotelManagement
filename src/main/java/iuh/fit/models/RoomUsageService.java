package iuh.fit.models;

import iuh.fit.utils.ErrorMessages;
import iuh.fit.utils.GlobalConstants;
import iuh.fit.utils.RegexChecker;

import java.util.Objects;

public class RoomUsageService {
    private String roomUsageServiceId;
    private int quantity;
    private HotelService hotelService;
    private ReservationForm reservationForm;

    public RoomUsageService() {
    }

    public RoomUsageService(String roomUsageServiceId, int quantity, HotelService hotelService) {
        this.setRoomUsageServiceId(roomUsageServiceId);
        this.setQuantity(quantity);
        this.setHotelService(hotelService);
        this.setReservationForm(reservationForm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomUsageService that = (RoomUsageService) o;
        return Objects.equals(roomUsageServiceId, that.roomUsageServiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomUsageServiceId);
    }

    public String getRoomUsageServiceId() {
        return roomUsageServiceId;
    }

    public void setRoomUsageServiceId(String roomUsageServiceId) {
        if (roomUsageServiceId == null || roomUsageServiceId.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.ROOM_USAGE_SERVICE_INVALID_ID_ISNULL);
        }
        if (!RegexChecker.isValidIDFormat(GlobalConstants.ROOMUSAGESERVICE_PREFIX, roomUsageServiceId)) {
            throw new IllegalArgumentException(ErrorMessages.ROOM_USAGE_SERVICE_INVALID_ID_FORMAT);
        }
        this.roomUsageServiceId = roomUsageServiceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException(ErrorMessages.ROOM_USAGE_SERVICE_INVALID_QUANTITY);
        this.quantity = quantity;
    }

    public HotelService getHotelService() {
        return hotelService;
    }

    public void setHotelService(HotelService hotelService) {
        if (hotelService == null)
            throw new IllegalArgumentException(ErrorMessages.NULL_HOTELSERVICE);
        this.hotelService = hotelService;
    }

    public ReservationForm getReservationForm() {
        return reservationForm;
    }

    public void setReservationForm(ReservationForm reservationForm) {
        this.reservationForm = reservationForm;
    }

    @Override
    public String toString() {
        return "RoomUsageService{" +
                "roomUsageServiceId='" + roomUsageServiceId + '\'' +
                ", quantity=" + quantity +
                ", hotelService=" + hotelService +
                '}';
    }
}
