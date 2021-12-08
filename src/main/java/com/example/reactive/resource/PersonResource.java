package com.example.reactive.resource;

import org.bson.types.ObjectId;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.example.reactive.domain.Person;

import io.smallrye.mutiny.Uni;

@Path("/persons")
public class PersonResource {

    @GET
    public Uni<List<Person>> list() {
        return Person.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Person> get(@PathParam("id") String id) {
        return Person.findById(id);
    }

    @POST
    public Uni<Response> create(Person person) {
        person.persist();
        return Uni.createFrom().item(Response.status(201).build());
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") String id, Person person) {
        person.update();
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        Person.findById(id).subscribe().with(person -> {

            person.delete();

        });
    }

    // @GET
    // @Path("/search/{name}")
    // public Uni<Person> search(@PathParam("name") String name) {
    //     return Person.findByName(name);
    // }

    @DELETE
    public void deleteAll(){
        Person.deleteAll();
    }
}