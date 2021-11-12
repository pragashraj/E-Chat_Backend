package com.echat.chat.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyChat {
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

    private String primaryContributor;

    private String secondaryContributor;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Chat> chats = new ArrayList<>();
}