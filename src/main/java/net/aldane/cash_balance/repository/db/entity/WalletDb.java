package net.aldane.cash_balance.repository.db.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet")
public class WalletDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "budget")
    private Float budget;
    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusDb status;

    @Column(name = "name", nullable = false,  unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDb user;
    @Column(name = "last_modification")
    private LocalDateTime lastModification;

    @Column(name = "creation_date")
    private LocalDateTime creation_date;

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }

    public UserDb getUser() {
        return user;
    }

    public void setUser(UserDb user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public StatusDb getStatus() {
        return status;
    }

    public void setStatus(StatusDb status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastModification() {
        return lastModification;
    }

    public void setLastModification(LocalDateTime lastModification) {
        this.lastModification = lastModification;
    }

    public LocalDateTime getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(LocalDateTime creation_date) {
        this.creation_date = creation_date;
    }
}
