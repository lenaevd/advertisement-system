package com.lenaevd.advertisements.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "sold_at")
    private LocalDate soldAt;

    public Sale(Advertisement advertisement, User customer) {
        this.advertisement = advertisement;
        this.customer = customer;
        this.soldAt = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", advertisementId=" + advertisement.getId() +
                ", customerId=" + customer.getId() +
                ", soldAt=" + soldAt +
                '}';
    }
}
