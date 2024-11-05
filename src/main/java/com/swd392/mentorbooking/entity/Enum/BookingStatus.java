package com.swd392.mentorbooking.entity.Enum;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    PROCESSING,
    SUCCESSFUL,
    DECLINED,
    ACCEPT,
    UNDECIDED;

    public String getMessage() {
        return switch (this) {
            case PROCESSING -> "Your Booking request has been processing.";
            case SUCCESSFUL -> "Your Booking request has been confirmed.";
            case DECLINED -> "You have declined the booking request.";
            case PENDING -> "Your invitation to group is pending.";
            case CONFIRMED -> "Your booking is confirmed.";
            case CANCELLED -> "Your booking has been cancelled.";
            case ACCEPT -> "Your invitation to group is accept.";
            case UNDECIDED -> "Your decision on the booking request is still undecided.";
            default -> "";
        };
    }
}
