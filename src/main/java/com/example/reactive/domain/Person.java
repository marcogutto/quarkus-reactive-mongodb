package com.example.reactive.domain;

import java.time.LocalDate;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class Person extends ReactivePanacheMongoEntity {

	public String name;

    @BsonProperty("birth")
    public LocalDate birthDate;

    public Status status;
}