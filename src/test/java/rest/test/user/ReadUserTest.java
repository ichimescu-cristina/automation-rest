package rest.test.user;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.model.user.Address;
import rest.model.user.Company;
import rest.model.user.Geo;
import rest.model.user.User;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadUserTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer testUserId;
    private static User testUserReturnedByGetCall;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        testUserId = getRandomUserId();

        WebTarget userWebTarget = webTarget.path("users").path(String.valueOf(testUserId));
        Invocation.Builder userInvocationBuilder = userWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response userResponse = userInvocationBuilder.get();
        try {
            testUserReturnedByGetCall = userResponse.readEntity(User.class);
        } catch (ResponseProcessingException e) {
            Assert.fail("The returned entity does not map correctly on the defined User entity.");
        }
        //verify status code
        Assert.assertEquals("Status code should be 200. ", 200, userResponse.getStatus());
    }

    @AfterClass
    public static void cleanUp() {
        testUserId = null;
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
    public void verifyIfUserIdIsValid() {
        Assert.assertTrue("UserId should be a positive Integer. ", testUserReturnedByGetCall.getId() > 0);
    }

    @Test
    public void verifyIfNameIsValid() {
        Pattern namePattern = Pattern.compile("^[a-zA-Z\\s]+");
        Matcher nameMatcher = namePattern.matcher(testUserReturnedByGetCall.getName());
        Assert.assertTrue("Name should only contain letters and spaces. ", nameMatcher.matches());
    }

    @Test
    public void verifyIfUserNameIsValid() {
        Pattern userNamePattern = Pattern.compile("[a-zA-Z0-9._\\-]{3,}");
        Matcher userNameMatcher = userNamePattern.matcher(testUserReturnedByGetCall.getUsername());
        Assert.assertTrue("UserName should have more than 3 characters. Only letters, digits, points, dashes and underscores are accepted.", userNameMatcher.matches());
    }

    @Test
    public void verifyIfEmailAddressIsValid() {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
        Matcher emailMatcher = emailPattern.matcher(testUserReturnedByGetCall.getEmail());
        Assert.assertTrue("Email format should match a specific expression.", emailMatcher.matches());
    }

    @Test
    public void verifyIfAddressIsValid() {
        Address address = testUserReturnedByGetCall.getAddress();

        Pattern streetPattern = Pattern.compile("[a-zA-Z+\\.\\s-]*");
        Matcher streetMatcher = streetPattern.matcher(address.getStreet());

        Pattern suitePattern = Pattern.compile("[a-zA-Z0-9\\s\\.]*");
        Matcher suiteMatcher = suitePattern.matcher(address.getSuite());

        Pattern cityPattern = Pattern.compile("[a-zA-Z+\\.\\s-]*");
        Matcher cityMatcher = cityPattern.matcher(address.getCity());

        Pattern zipcodePattern = Pattern.compile("[0-9-]{3,}");
        Matcher zipcodeMatcher = zipcodePattern.matcher(address.getZipcode());

        Assert.assertTrue("Address should respect the agreed format.", streetMatcher.matches() && suiteMatcher.matches() && cityMatcher.matches() && zipcodeMatcher.matches() && isGeoValid(address.getGeo()));
    }


    private boolean isGeoValid(Geo geo) {
        return (Double.parseDouble(geo.getLat()) < 90) && (Double.parseDouble(geo.getLat()) > -90) && (Double.parseDouble(geo.getLng()) > -180) && (Double.parseDouble(geo.getLng()) < 180);
    }

    @Test
    public void verifyIfPhoneIsValid() {
        System.out.println(testUserReturnedByGetCall.getPhone());
        Pattern phonePattern = Pattern.compile("[0-9-+().x\\s]{9,25}");
        Matcher phoneMatcher = phonePattern.matcher(testUserReturnedByGetCall.getPhone());
        Assert.assertTrue("Phone number should respect the agreed format.", phoneMatcher.matches());
    }

    @Test
    public void verifyIfCompanyIsValid() {
        Company company = testUserReturnedByGetCall.getCompany();
        System.out.println(company);

        Pattern namePattern = Pattern.compile("[a-zA-Z\\s-]*");
        Matcher nameMatcher = namePattern.matcher(company.getName());

        Pattern catchPhrasePattern = Pattern.compile("[a-zA-Z\\s-]*");
        Matcher catchPhraseMatcher = catchPhrasePattern.matcher(company.getCatchPhrase());

        Pattern bsPattern = Pattern.compile("[a-zA-Z\\s-]*");
        Matcher bsMatcher = bsPattern.matcher(company.getBs());

        Assert.assertTrue("Company should respect the agreed format.", (nameMatcher.matches() && (catchPhraseMatcher.matches()) && bsMatcher.matches()));
    }

    @Test
    public void verifyIfWebsiteIsValid() {
        System.out.println(testUserReturnedByGetCall.getWebsite());
        Pattern websitePattern = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?[a-zA-Z0-9./]+$", Pattern.CASE_INSENSITIVE);
        Matcher websiteMatcher = websitePattern.matcher(testUserReturnedByGetCall.getWebsite());
        Assert.assertTrue("Website should respect the agreed format.", websiteMatcher.matches());
    }
}
