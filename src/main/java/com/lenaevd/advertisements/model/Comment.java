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
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Comment(Advertisement advertisement, User author, String content) {
        this.advertisement = advertisement;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", advertisementId=" + advertisement.getId() +
                ", authorId=" + author.getId() +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
