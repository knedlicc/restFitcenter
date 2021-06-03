package cz.cvut.kbss.ear.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
//@JsonIgnoreProperties({"lecturesReservations, sportRoomReservations"})
@Entity
public class Room extends AbstractEntity {
    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    private Integer capacity;

    @JsonIgnore
    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = false)
    private SportCenter sportCenter;

//    @JsonIgnore
    @OneToMany(mappedBy = "room")
    @OrderBy("startTime DESC")
    private List<GroupLecture> groupLectures;

//    @JsonIgnore
    @OneToMany(mappedBy = "sportRoom")
    @OrderBy("sportRoom")
    private List<SportRoomReservation> sportRoomReservations;

    @Basic(optional = true)
    private BigDecimal priceForHourReservation;

    public List<GroupLecture> getLecturesReservations() {
        return groupLectures;
    }

    public List<SportRoomReservation> getRoomsReservations() {
        return sportRoomReservations;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPriceForHourReservation() {
        return priceForHourReservation;
    }

    public SportCenter getSportCenter() {
        return sportCenter;
    }

    public void setSportCenter(SportCenter sportCenter) {
        this.sportCenter = sportCenter;
    }

    public List<GroupLecture> getGroupLectures() {
        return groupLectures;
    }

    public void setGroupLectures(List<GroupLecture> groupLectures) {
        this.groupLectures = groupLectures;
    }

    public List<SportRoomReservation> getSportRoomReservations() {
        return sportRoomReservations;
    }

    public void setSportRoomReservations(List<SportRoomReservation> sportRooms) {
        this.sportRoomReservations = sportRooms;
    }

    public Room() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return getName().equals(room.getName()) &&
                getCapacity().equals(room.getCapacity()) &&
                getSportCenter().equals(room.getSportCenter()) &&
                Objects.equals(getGroupLectures(), room.getGroupLectures()) &&
                Objects.equals(getSportRoomReservations(), room.getSportRoomReservations()) &&
                getPriceForHourReservation().equals(room.getPriceForHourReservation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCapacity(), getSportCenter(), getGroupLectures(), getSportRoomReservations(), getPriceForHourReservation());
    }

    public void addGroupLecture(GroupLecture lecture) {
        Objects.requireNonNull(lecture);
        this.groupLectures.add(lecture);
    }

    public void addSportRoomReservation(SportRoomReservation reservation) {
        Objects.requireNonNull(reservation);
        sportRoomReservations.add(reservation);
    }

    public void setPriceForHourReservation(BigDecimal priceForHourReservation) {
        this.priceForHourReservation = priceForHourReservation;
    }

    //    public void removeLecture(GroupLecture lecture) {
//        Objects.requireNonNull(lecture);
//        groupLectures.remove(lecture);
//    }
}
