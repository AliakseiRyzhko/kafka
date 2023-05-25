package com.example.consumer.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client {

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    private String clientName;
    @Column(name = "email")
    private String email;
    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Transaction> transactions;
    @Column(name = "is_fake_client")
    private Boolean isFakeClient;

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", clientName='" + clientName + '\'' +
                ", email='" + email + '\'' +
                ", isFakeClient=" + isFakeClient +
                '}';
    }
}
