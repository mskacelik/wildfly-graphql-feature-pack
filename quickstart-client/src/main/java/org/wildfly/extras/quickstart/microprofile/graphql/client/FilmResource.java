package org.wildfly.extras.quickstart.microprofile.graphql.client;

import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.core.Document;
import io.smallrye.graphql.client.core.ScalarType;
import io.smallrye.graphql.client.core.Variable;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClientBuilder;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.wildfly.extras.quickstart.microprofile.graphql.client.model.Film;
import org.wildfly.extras.quickstart.microprofile.graphql.client.model.Hero;

import java.util.Collections;
import java.util.List;

import static io.smallrye.graphql.client.core.Argument.arg;
import static io.smallrye.graphql.client.core.Argument.args;
import static io.smallrye.graphql.client.core.Document.document;
import static io.smallrye.graphql.client.core.Field.field;
import static io.smallrye.graphql.client.core.Operation.operation;
import static io.smallrye.graphql.client.core.Variable.var;
import static io.smallrye.graphql.client.core.Variable.vars;
import static io.smallrye.graphql.client.core.VariableType.nonNull;

@Path("/")
public class FilmResource {

    private static final String url;

    static {
        final Integer port = Integer.getInteger("port", 8080);
        url = "http://localhost:" + port + "/quickstart/graphql";
    }

    @GET
    @Path("/typesafe/films")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Film> getAllFilms() {
        System.out.println("Using URL " + url);
        FilmClientApi client = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(url)
                .build(FilmClientApi.class);
        return client.getAllFilms();
    }

    @GET
    @Path("/typesafe/films/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Film getFilm(@PathParam("id") int index) {
        System.out.println("Using URL " + url);
        FilmClientApi client = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(url)
                .build(FilmClientApi.class);
        return client.getFilm(index);
    }

    @GET
    @Path("/typesafe/delete/hero/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Hero deleteFilm(@PathParam("id") int id) {
        System.out.println("Using URL " + url);
        FilmClientApi client = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(url)
                .build(FilmClientApi.class);
        return client.deleteHero(id);
    }

    @GET
    @Path("/typesafe/heroes/{surname}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Hero> getHeroesWithSurname(@PathParam("surname") String surname) {
        System.out.println("Using URL " + url);
        FilmClientApi client = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(url)
                .build(FilmClientApi.class);
        return client.getHeroesWithSurname(surname);
    }

    @GET
    @Path("/dynamic/films/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Film getFilmDynamic(@PathParam("id") int id) throws Exception {
        try (DynamicGraphQLClient dynamicGraphQLClient = DynamicGraphQLClientBuilder
                .newBuilder()
                .url(url)
                .build()
        ){
            Variable filmId = var("filmId", nonNull(ScalarType.GQL_INT));
            Document query = document(
                    operation("film", vars(filmId),
                            field("film", args(arg("filmId", filmId)),
                                    field("title"),
                                    field("episodeID"),
                                    field("director"),
                                    field("releaseDate"),
                                    field("desc")
                            )
                    )
            );
            Response response = dynamicGraphQLClient.executeSync(query, Collections.singletonMap("filmId", id));
            return response.getObject(Film.class, "film");
        }
    }
}
