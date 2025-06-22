package com.lgcns.aidd.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "floor")
@Getter
@Setter
public class Floor extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer floor;

    private Integer width;

    private Integer length;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "floor")
    private List<Seat> seats;
}
