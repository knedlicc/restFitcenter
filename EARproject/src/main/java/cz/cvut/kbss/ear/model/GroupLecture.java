package cz.cvut.kbss.ear.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Entity
@NamedQueries({
        @NamedQuery(name = "GroupLecture.findByUser", query = "SELECT g from GroupLecture g WHERE :user MEMBER OF g.usersL"),
        @NamedQuery(name = "GroupLecture.findByRoom", query = "SELECT g from GroupLecture g WHERE :room = g.room")
})

public class GroupLecture extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    private String description;

    @Basic(optional = false)
    @Column(nullable = false)
    private int capacity;

    @Basic(optional = false)
    @Column(nullable = false)
    private int occupancy;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Basic(optional = false)
    @Column(nullable = false)
    private BigDecimal price;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private Room room;

    @Basic(optional = false)
    @Column(nullable = false)
    private boolean recurring;

    public GroupLecture() {
    }

    public GroupLecture(GroupLecture groupLecture) { //copy constructor
        this.name = groupLecture.getName();
        this.capacity = groupLecture.getCapacity();
        this.startTime = groupLecture.getStartTime();
        this.endTime = groupLecture.getEndTime();
        this.price = groupLecture.getPrice();
        this.recurring = groupLecture.isRecurring();
        this.room = groupLecture.getRoom();
        this.description = groupLecture.getDescription();
        this.usersL = null;
    }

    public GroupLecture(String name, int capacity, LocalDateTime startTime, LocalDateTime endTime, BigDecimal price, boolean recurring, Room room) {
        this.name = name;
        this.capacity = capacity;
        this.occupancy = 0;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.recurring = recurring;
        this.room = room;
    }


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "users_lectures",
            joinColumns = {@JoinColumn(name = "grouplecture_id")},
            inverseJoinColumns = {@JoinColumn(name = "absractuser_id")}
    )
    private List<User> usersL;


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<User> getUsersL() {
        return usersL;
    }

    public void setUsersL(List<User> usersL) {
        this.usersL = usersL;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void addUser(User user) {
        Objects.requireNonNull(user);
        if (usersL == null) {
            usersL = new ArrayList<>();
        }
        usersL.add(user);
    }

    public void setLectureCapacity(int lectureCapacity) {
        this.capacity = lectureCapacity;
    }
}
