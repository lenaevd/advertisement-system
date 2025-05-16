package com.lenaevd.advertisements.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @Transient
    private User seller;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    @Transient
    private Message lastMessage;

    public Chat(Advertisement advertisement, User customer) {
        this.advertisement = advertisement;
        this.customer = customer;
    }

    @PostLoad
    public void setSellerAndLastMessage() {
        this.seller = this.advertisement.getSeller();
        this.lastMessage = findLastMessage();
    }

    private Message findLastMessage() {
        return messages.stream()
                .max(Comparator.comparing(Message::getSentAt))
                .orElse(null);
    }

    public List<User> getMembers() {
        return List.of(seller, customer);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", advertisementId=" + advertisement.getId() +
                ", sellerId=" + seller.getId() +
                ", customerId=" + customer.getId() +
                '}';
    }
}
