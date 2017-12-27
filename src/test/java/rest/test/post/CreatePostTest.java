package rest.test.post;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.model.Post;
import rest.model.user.User;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;

public class CreatePostTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer randomUserId;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        randomUserId = getRandomUserId();
    }

    @AfterClass
    public static void cleanUp() {
        randomUserId = null;
        webTarget = null;
        client.close();
        client = null;
    }

    private static Integer getRandomUserId() {
        WebTarget usersWebTarget = webTarget.path("users");
        Invocation.Builder usersInvocationBuilder = usersWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        List<User> users = usersInvocationBuilder.get(new GenericType<List<User>>() {
        });
        return new Random().nextInt(users.size()) + 1;
    }

    @Test
    public void testCreatePost() {
        WebTarget postsWebTarget = webTarget.path("posts");
        Invocation.Builder postsInvocationBuilder = postsWebTarget.request();
        Post newPost = new Post();
        newPost.setUserId(randomUserId);
        newPost.setTitle("Created Post Title");
        newPost.setBody("Created Post Body");
        Response postResponse = postsInvocationBuilder.post(Entity.entity(newPost, MediaType.APPLICATION_JSON_TYPE));
        //verify status code
        Assert.assertEquals("Expected status code: 201 ", 201, postResponse.getStatus());
        postResponse.close();
        //verify postResponse
        WebTarget postWebTarget = client.target(postResponse.getLocation());
        Invocation.Builder postInvocationBuilder = postWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        try {
            //do a GET to see if the data was correctly added
            Response getCreatedPostResponse = postInvocationBuilder.get();
            Assert.assertEquals("Expected status code: 200", 200, getCreatedPostResponse.getStatus());
            Post retrievedPost = getCreatedPostResponse.readEntity(Post.class);
            Assert.assertEquals("userId is not valid ", newPost.getUserId(), retrievedPost.getUserId());
            Assert.assertEquals("Title is not valid.", newPost.getTitle(), retrievedPost.getTitle());
            Assert.assertEquals("Body is not valid.", newPost.getBody(), retrievedPost.getBody());
        } catch (ResponseProcessingException e) {
            Assert.fail("Retrieved entity is not the same type of the created entity.");
        }
    }
}
