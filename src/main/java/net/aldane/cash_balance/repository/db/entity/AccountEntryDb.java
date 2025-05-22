package net.aldane.cash_balance.repository.db.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_entry")
public class AccountEntryDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "amount")
    private Float amount;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private WalletDb wallet;
    @Column(name = "comment")
    private String comment;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryDb category;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusDb status;

    @Column(name = "date")
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandDb brand;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreDb store;

    @ManyToOne
    @JoinColumn(name = "type_operation_id")
    private AccountEntryTypeDb accountEntryType;

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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public WalletDb getWallet() {
        return wallet;
    }

    public void setWallet(WalletDb wallet) {
        this.wallet = wallet;
    }

    public CategoryDb getCategory() {
        return category;
    }

    public void setCategory(CategoryDb category) {
        this.category = category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BrandDb getBrand() {
        return brand;
    }

    public void setBrand(BrandDb brand) {
        this.brand = brand;
    }

    public StoreDb getStore() {
        return store;
    }

    public void setStore(StoreDb store) {
        this.store = store;
    }

    public AccountEntryTypeDb getAccountEntryType() {
        return accountEntryType;
    }

    public void setAccountEntryType(AccountEntryTypeDb accountEntryType) {
        this.accountEntryType = accountEntryType;
    }
}
