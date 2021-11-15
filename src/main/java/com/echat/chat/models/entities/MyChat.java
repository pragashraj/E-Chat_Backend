package com.echat.chat.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyChat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generic-generator")
    @GenericGenerator(name = "generic-generator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "M"),
                    @org.hibernate.annotations.Parameter(name = "digits", value = "9"),
                    @org.hibernate.annotations.Parameter(name = "initial_id", value = "100000000"),
            },
            strategy = "com.echat.chat.utils.GenericIdGenerator")
    private String id;

    @OneToMany(mappedBy = "myChat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Chat> chats;
}