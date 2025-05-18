package com.lenaevd.advertisements.model;

import com.lenaevd.advertisements.authentication.CustomUserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements CustomUserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;
    private String username;
    private String email;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "seller")
    private List<Grade> grades;

    @Transient
    private float rating;

    @OneToMany(mappedBy = "customer")
    private List<Chat> customerChats;

    @OneToMany(mappedBy = "customer")
    private List<Chat> sellerChats;

    @OneToMany(mappedBy = "seller")
    private List<Advertisement> advertisements;

    @OneToMany(mappedBy = "customer")
    private List<Sale> purchasedItems;

    @Transient
    private List<Sale> soldItems;

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @PostLoad
    public void setSalesAndRating() {
        this.soldItems = findSales(this.advertisements);
        this.rating = calculateRating(this.grades);
    }

    private List<Sale> findSales(List<Advertisement> advertisements) {
        List<Sale> sales = new ArrayList<>();
        for (Advertisement ad : advertisements) {
            if (ad.getSale() != null && ad.getStatus() == AdvertisementStatus.COMPLETED) {
                sales.add(ad.getSale());
            }
        }
        return sales;
    }

    private float calculateRating(List<Grade> grades) {
        int sum = 0;
        for (Grade grade : grades) {
            sum += grade.getNumber();
        }
        return round((float) sum / grades.size());
    }

    private float round(float value) {
        return (float) Math.round(value * 10) / 10;
    }

    public List<Chat> getChats() {
        return Stream.concat(customerChats.stream(), sellerChats.stream()).toList();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", rating=" + rating +
                ", advertisements=" + advertisements +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof User user)) {
            return false;
        }
        return id == user.id && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
