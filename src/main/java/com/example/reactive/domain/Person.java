package com.example.reactive.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.ws.rs.NotFoundException;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class Person extends ReactivePanacheMongoEntityBase {
    
    @BsonId
	public String id;

    public String name;

    @BsonProperty("birth")
    public LocalDate birthDate;

    public Status status;

    public LocalDateTime creationDate;

    public static Multi<Person> streamAllPerson() {
        return streamAll();
    }

    public static Uni<Person> update(String id, Person updatePerson) {
        Uni<Person> personUni = Person.findById(id);
        return personUni
                .onItem().transform(person -> {
                    person.setName(updatePerson.getName());
                    person.setBirthDate(updatePerson.getBirthDate());
                    person.setStatus(updatePerson.getStatus());
                    return person;
                }).call(person -> person.persistOrUpdate());
    }

    public static Uni<Void> delete(String id) {
        Uni<Person> personUni = findById(id);

        return personUni
                .onItem().ifNull().failWith(() -> new NotFoundException())
                .chain(person -> {
                    return person.delete();
                });
    }
}
