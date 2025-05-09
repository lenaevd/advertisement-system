package com.lenaevd.advertisements.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private int id;
    private int number;

    @ManyToOne()
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Grade(int number, User seller, User customer) {
        this.number = number;
        this.seller = seller;
        this.customer = customer;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", number=" + number +
                ", sellerId=" + seller.getId() +
                ", customerId=" + customer.getId() +
                '}';
    }
}
