package com.example.reactive.resource;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.reactive.domain.Person;
import com.example.reactive.domain.Status;

import org.bson.types.ObjectId;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/persons")
public class PersonResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Person> stream() {
        return Person.streamAllPerson();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> findById(@PathParam("id") String id) {
        return Person.findById(id);
    }

    @GET
    @Path("/search")
    public Uni<List<Person>> search(@QueryParam("name") String name, @QueryParam("status") Status status,
                                  @QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {
        if (name != null) {
            return Person.find("{'name': ?1,'status': ?2}", name, status).list();
        }
        return Person
                .find("{'creationDate': {$gte: ?1}, 'creationDate': {$lte: ?2}}", ZonedDateTime.parse(dateFrom).toLocalDateTime(),
                        ZonedDateTime.parse(dateTo).toLocalDateTime()).list();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(Person person) {
        person.creationDate = LocalDateTime.now();
        return person.<Person>persist().map(v ->
                Response.created(URI.create("/persons/" + v.id.toString()))
                        .entity(person).build());
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Person> update(@PathParam("id") String id, Person person) {
        return Person.update(id, person);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") String id) {
        return Person.delete(id);
    }

    // @GET
    // @Path("/search2")
    // public Uni<List<Post>> searchCustomQueries(@QueryParam("authors") List<String> authors) {

        // using Document
        // return Post.find(new Document("author", new Document("$in", authors))).list();

        // using a raw JSON query
        //Post.find("{'$or': {'author':John Doe, 'author':Grace Kelly}}");
        //Post.find("{'author': {'$in': [John Doe, Grace Kelly]}}");

        // using Panache QL
        //Post.find("author in (John Doe,Grace Kelly)");

    // }

    // @PUT
    // @Path("/{id}/comment")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    // public Uni<Response> addCommentToPost(@PathParam("id") String id, Comment comment) {
    //     return Post.addCommentToPost(comment, id).map(v -> Response.accepted(v).build());
    // }
}