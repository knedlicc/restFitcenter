package cz.cvut.kbss.ear.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(name = "PermanentCard.findByUser", query = "SELECT g from PermanentCard g WHERE :user =g.user")
})
public class PermanentCard extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    private int code;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime validTo;

    @OneToOne(mappedBy = "card")
    private User user;

    private boolean isValid;

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public PermanentCard(int code, LocalDateTime validFrom, LocalDateTime validTo, User user, boolean isValid) {
        this.code = code;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.user = user;
        this.isValid = isCardValid();
    }

    public boolean isCardValid() {
        return LocalDateTime.now().isBefore(validTo) && LocalDateTime.now().isAfter(validFrom);
    }

    public boolean getValid() {
        return this.isValid;
    }

    public PermanentCard() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isValid() {
        return isValid;
    }
}
