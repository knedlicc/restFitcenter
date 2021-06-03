package cz.cvut.kbss.ear.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.kbss.ear.exception.LectureIsAlreadyRegisteredException;
import cz.cvut.kbss.ear.exception.UserIsNotRegisteredForThisLectureException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "EAR_USER")
@NamedQueries({
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findByGroupLecture", query = "SELECT u from User u WHERE :groupLecture MEMBER OF u.lectures")
})

public class User extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    private String email;

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    private String surname;

    @Basic(optional = false)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;


    @Basic(optional = false)
    @Column(nullable = false)
    private BigDecimal balance;

    @JsonIgnore
    @ManyToMany(mappedBy = "usersL", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    List<GroupLecture> lectures;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<SportRoomReservation> roomReservations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permanentcard_id", referencedColumnName = "id")
    private PermanentCard card;

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public List<GroupLecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<GroupLecture> authoredLectures) {
        this.lectures = authoredLectures;
    }

    public List<SportRoomReservation> getRoomReservations() {
        return roomReservations;
    }

    public void setRoomReservations(List<SportRoomReservation> authoredRooms) {
        this.roomReservations = authoredRooms;
    }

    public PermanentCard getCard() {
        return card;
    }

    public void setCard(PermanentCard card) {
        this.card = card;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public void removeGroupLecture(GroupLecture groupLecture) {
        Objects.requireNonNull(groupLecture);
        if (getLectures().contains(groupLecture)) {
            getLectures().remove(groupLecture);
        } else throw new UserIsNotRegisteredForThisLectureException("The user is not registered for that lecture");
    }

    public void registerForLecture(GroupLecture groupLecture) {
        Objects.requireNonNull(groupLecture);
        if (lectures == null){
            lectures = new ArrayList<>();
        }
        if (!lectures.contains(groupLecture)) {
            lectures.add(groupLecture);
            groupLecture.addUser(this);
        } else throw new LectureIsAlreadyRegisteredException("The client is already registered for that lecture");
    }

    public boolean isAdmin() {
        return role == Roles.ADMIN;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }
}
