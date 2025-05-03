package com.lenaevd.advertisements.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "advertisements")
@Getter
@Setter
@NoArgsConstructor
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advertisement_id")
    private int id;
    private String title;
    private String content;
    private int price;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @Enumerated(EnumType.STRING)
    private AdvertisementType type;

    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "premium_expiry_date")
    private LocalDateTime premiumExpiryDate;

    @OneToMany(mappedBy = "advertisement")
    private List<Comment> comments;

    @OneToOne(mappedBy = "advertisement")
    private Sale sale;

    @Override
    public String toString() {
        return "Advertisement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", seller=" + seller +
                ", status=" + status +
                '}';
    }

    //    private Sale getSaleIfNotNull() {
//        if (status == AdvertisementStatus.COMPLETED && sale != null) {
//            return sale;
//        }
//    }
}
