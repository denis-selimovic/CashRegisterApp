package ba.unsa.etf.si.utility.json;

import ba.unsa.etf.si.models.User;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {


    public UserDeserializer() {
        this(null);
    }

    public UserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String name = node.get("name").asText(), surname = node.get("surname").asText();
        String address = node.get("address").asText(), city = node.get("city").asText(), country = node.get("country").asText();
        String email = node.get("email").asText(), phoneNumber = node.get("phoneNumber").asText();
        String username = node.get("username").asText();
        User.UserRole role = User.UserRole.valueOf(node.get("roles").get(0).get("rolename").asText());

        return new User(name, surname, address, city, country, phoneNumber, email, username, role);
    }

    public static User getUserFromResponse(String response) throws JsonProcessingException {
        ObjectMapper userMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        userMapper.registerModule(module);
        return userMapper.readValue(response, User.class);
    }
}
