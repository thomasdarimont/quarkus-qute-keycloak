package dev.morling.demos.quarkus;

import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Path("/todo")
public class TodoResource {

    @Inject
    Template error;

    @Inject
    Template todo;

    @Inject
    Template todos;

    @Inject
    TodoUser currentUser;

    final List<Integer> priorities = IntStream.range(1, 6).boxed().collect(Collectors.toList());

    @GET
    @Consumes({MediaType.TEXT_HTML, MediaType.MEDIA_TYPE_WILDCARD})
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listTodos(@QueryParam("filter") String filter) {
        return todos.data("todos", find(filter))
                .data("priorities", priorities)
                .data("filter", filter)
                .data("user", currentUser)
                .data("filtered", filter != null && !filter.isEmpty());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Todo> listTodosJson(@QueryParam("filter") String filter) {
        return find(filter);
    }

    private List<Todo> find(String filter) {
        Sort sort = Sort.ascending("completed")
                .and("priority", Direction.Descending)
                .and("title", Direction.Ascending);

        if (filter != null && !filter.isEmpty()) {
            return Todo.find("userId = ?1 and LOWER(title) LIKE LOWER(?2)", sort,
                    currentUser.getUserId(), "%" + filter + "%").list();
        } else {
            return Todo.find("userId = ?1", sort, currentUser.getUserId()).list();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    @Path("/new")
    public Response addTodo(@MultipartForm TodoForm todoForm) {
        Todo todo = todoForm.convertIntoTodo();
        todo.userId = currentUser.getUserId();
        todo.persist();

        return Response.status(303)
                .location(URI.create("/todo"))
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{id}/edit")
    public TemplateInstance updateForm(@PathParam("id") long id) {
        Todo loaded = Todo.find("id=?1 and userId=?2", id, currentUser.getUserId()).firstResult();

        if (loaded == null) {
            return error.data("error", "Todo with id " + id + " does not exist for current user.");
        }

        return todo.data("todo", loaded)
                .data("priorities", priorities)
                .data("update", true);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    @Path("/{id}/edit")
    public Object updateTodo(
            @PathParam("id") long id,
            @MultipartForm TodoForm todoForm) {

        Todo loaded = Todo.find("id=?1 and userId=?2", id, currentUser.getUserId()).firstResult();

        if (loaded == null) {
            return error.data("error", "Todo with id " + id + " has been deleted after loading this form.");
        }

        loaded = todoForm.updateTodo(loaded);

        return Response.status(303)
                .location(URI.create("/todo"))
                .build();
    }

    @POST
    @Transactional
    @Path("/{id}/delete")
    public Response deleteTodo(@PathParam("id") long id) {

        long deleted = Todo.delete("id=?1 and userId=?2", id, currentUser.getUserId());
        if (deleted == 0L) {
            return Response.status(404)
                    .build();
        }

        return Response.status(303)
                .location(URI.create("/todo"))
                .build();
    }
}
