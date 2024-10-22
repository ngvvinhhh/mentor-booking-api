package com.swd392.mentorbooking.entity.Enum;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    PROCESSING,
    SUCCESSFUL,
    DECLINED;

    public String getMessage() {
        switch (this) {
            case PROCESSING:
                return "You have a new booking request.";
            case SUCCESSFUL:
                return "Your Booking request has been confirmed.";
            case DECLINED:
                return "You have declined the booking request.";
            case PENDING:
                return "Your booking is pending confirmation.";
            case CONFIRMED:
                return "Your booking is confirmed.";
            case CANCELLED:
                return "Your booking has been cancelled.";
            case COMPLETED:
                return "Your booking has been completed.";
            default:
                return "";
        }
    }
}
