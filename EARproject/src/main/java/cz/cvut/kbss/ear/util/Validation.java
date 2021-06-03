package cz.cvut.kbss.ear.util;

import cz.cvut.kbss.ear.model.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Validation {
    public static boolean timeIsValidForReservation(LocalDateTime sDateTime, LocalDateTime eDateTime, Room room) {
        return ((timeIsFreeInSchedule(room, sDateTime, eDateTime)
                && reservationIsInOpenHours(room.getSportCenter(), sDateTime.toLocalTime(), eDateTime.toLocalTime()))
                && startIsBeforeEnd(sDateTime, eDateTime)
                && eventStartsAndEndsAtTheSameDay(sDateTime, eDateTime)
        );
    }

    public static boolean reservationIsInOpenHours(SportCenter sportCenter, LocalTime reservationStartTime, LocalTime reservationEndTime) {
        return sportCenter.getOpenFrom().isBefore(reservationStartTime) && sportCenter.getOpenTo().isAfter(reservationEndTime);
    }

    public static boolean durationIsLongEnough(SportRoomReservation reservation) {
        Duration duration = Duration.between(reservation.getStartTime(), reservation.getEndTime());
        return duration.toMinutes() >= 30;
    }

    public static boolean durationIsLongEnough(GroupLecture lecture) {
        Duration duration = Duration.between(lecture.getStartTime(), lecture.getEndTime());
        return duration.toMinutes() >= 15;
    }

    //when client wants to sign up for a group lecture
    public static boolean capacityIsEnoughForSignUp(GroupLecture groupLecture) {
        return groupLecture.getOccupancy() < groupLecture.getCapacity();
    }

    public static boolean userBalanceIsEnough(BigDecimal reservationPrice, User client) {
        return client.getBalance().compareTo(reservationPrice) > 0;
    }

    public static boolean eventStartsAndEndsAtTheSameDay(LocalDateTime sTime, LocalDateTime eTime) {
        return sTime.toLocalDate().equals(eTime.toLocalDate());
    }


    public static boolean timeIsFreeInSchedule(Room room,
                                               LocalDateTime startTime, LocalDateTime endTime) {
        List<SportRoomReservation> roomsReservations = room.getRoomsReservations();
        List<GroupLecture> lectureReservations = room.getLecturesReservations();
        //      check for roomReservations
        //if period on of the already existing lectures contains new lecture beginning or end, this time is not available, function returns false
        for (SportRoomReservation roomReservation : roomsReservations) {
            if ((startTime.isAfter(roomReservation.getStartTime()) || (startTime.isEqual(roomReservation.getStartTime())))
                    && (startTime.isBefore(roomReservation.getEndTime()) || startTime.isEqual(roomReservation.getEndTime()))) {
                return false;
            }
            if ((endTime.isAfter(roomReservation.getStartTime()) || endTime.isEqual(roomReservation.getStartTime()))
                    && (endTime.isBefore(roomReservation.getEndTime()) || endTime.isEqual(roomReservation.getEndTime()))) {
                return false;
            }
        }

        //      check for lectures
        for (GroupLecture groupLecture : lectureReservations) {
            if ((startTime.isAfter(groupLecture.getStartTime()) || startTime.isEqual(groupLecture.getStartTime())) &&
                    ((startTime.isBefore(groupLecture.getEndTime())) || (startTime.isEqual(groupLecture.getEndTime())))) {
                return false;
            }
            if (endTime.isAfter(groupLecture.getStartTime()) && endTime.isBefore(groupLecture.getEndTime())) {
                return false;
            }
        }
        return true;
    }

    public static Boolean startIsBeforeEnd(LocalDateTime startTime, LocalDateTime endTime) {
        return endTime.isAfter(startTime);
    }
}
