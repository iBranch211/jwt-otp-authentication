package com.mist.mist_backend.database.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Otp entity class
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    @Id
    @Column(name = "user_id")
    private Integer id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    private String otpCode;
    private Long createdAt;
    private Long expiresAt;
}
