package net.aldane.cash_balance.repository.db.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class RoleDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusDb status;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
