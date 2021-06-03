package cz.cvut.kbss.ear.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "SportRoomReservation.findByRoom", query = "SELECT g from SportRoomReservation g WHERE :room = g.sportRoom")
})
public class SportRoomReservation extends AbstractEntity {


    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Basic(optional = false)
    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Room sportRoom;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    public SportRoomReservation() {
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Room getSportRoom() {
        return sportRoom;
    }

    public User getOwner() {
        return owner;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setSportRoom(Room sportRoom) {
        this.sportRoom = sportRoom;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void addUser(User user) {
        Objects.requireNonNull(user);
        this.setOwner(user);
    }
}
