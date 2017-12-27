package rest.test.todo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.model.Todo;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadTodoTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer testTodoId;
    private static Todo testTodoReturnedByGetCall;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        testTodoId = getRandomTodoId();

        WebTarget todoWebTarget = webTarget.path("todos").path(String.valueOf(testTodoId));
        Invocation.Builder todoInvocationBuilder = todoWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response todoResponse = todoInvocationBuilder.get();
        try {
            testTodoReturnedByGetCall = todoResponse.readEntity(Todo.class);
        } catch (ResponseProcessingException e) {
            Assert.fail("The returned entity does not map correctly on the defined Todo entity.");
        }
        //verify status code
        Assert.assertEquals("Status code should be 200. ", 200, todoResponse.getStatus());
    }

    @AfterClass
    public static void cleanUp() {
        testTodoId = null;
        webTarget = null;
        client.close();
        client = null;
    }

    private static Integer getRandomTodoId() {
        WebTarget todoWebTarget = webTarget.path("todos");
        Invocation.Builder todoInvocationBuilder = todoWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        List<Todo> todos = todoInvocationBuilder.get(new GenericType<List<Todo>>() {
        });
        return new Random().nextInt(todos.size()) + 1;
    }

    @Test
    public void verifyIfUserIdIsValid() {
        Assert.assertTrue("UserId should be a positive Integer. ", testTodoReturnedByGetCall.getUserId() > 0);
    }

    @Test
    public void verifyIfIdIsValid() {
        Assert.assertTrue("Id should be a positive Integer. ", testTodoReturnedByGetCall.getId() > 0);
    }

    @Test
    public void verifyIfTitleIsValid() {
        Pattern titlePattern = Pattern.compile("[a-zA-Z0-9._\\-\n\\s]{3,}");
        Matcher titleMatcher = titlePattern.matcher(testTodoReturnedByGetCall.getTitle());
        Assert.assertTrue("Title should have more than 3 characters. Only letters, digits, points, dashes and underscores are accepted.", titleMatcher.matches());
    }

    @Test
    public void verifyIfCompletedIsValid() {
        Assert.assertTrue("Completed should be a valid boolean value. ", ((testTodoReturnedByGetCall.getCompleted() == true) || (testTodoReturnedByGetCall.getCompleted() == false)));
    }
}
