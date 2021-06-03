package cz.cvut.kbss.ear.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class SportCenter extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalTime openFrom;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalTime openTo;

    @Basic(optional = false)
    @Column(nullable = false)
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "sportCenter")
    @OrderBy("capacity DESC")
    private List<Room> rooms;

    public SportCenter() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getOpenFrom() {
        return openFrom;
    }

    public void setOpenFrom(LocalTime openFrom) {
        this.openFrom = openFrom;
    }

    public LocalTime getOpenTo() {
        return openTo;
    }

    public void setOpenTo(LocalTime openTo) {
        this.openTo = openTo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void addRoom(Room room) {
        Objects.requireNonNull(room);
        if (rooms == null) {
            this.rooms = new ArrayList<>();
        }
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        Objects.requireNonNull(room);
        if (rooms.contains(room)) {
            rooms.remove(room);
        }
    }
}
