package net.conczin.archlink;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.conczin.archlink.utils.ConfigService;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RoleAPI {
    private final Gson Gson = new Gson();

    public static final RoleAPI INSTANCE = new RoleAPI();

    public static RoleAPI getInstance() {
        return INSTANCE;
    }

    public void get(String uuid, Consumer<UserRoles> callback) {
        CompletableFuture.supplyAsync(() -> {
            fetchUserData(uuid, callback);
            return 0;
        });
    }

    private void fetchUserData(String uuid, Consumer<@Nullable UserRoles> callback) {
        try {
            // Construct the URL for the API request
            URL url = new URL("https://api.conczin.net/v1/minecraft/" + ConfigService.getInstance().getGuildId() + "/" + uuid.replace("-", ""));

            // Create an HttpURLConnection and set request properties
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String content = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

                // Parse the JSON response
                JsonObject jsonObject = Gson.fromJson(content, JsonObject.class);

                // Create a Set for the roles
                Set<String> roles = new HashSet<>();
                boolean linked = false;
                if (jsonObject.has("roles")) {
                    Arrays.stream(jsonObject.get("roles").getAsString().split(",")).map(String::trim).forEach(roles::add);
                    linked = true;
                }

                UserRoles userRoles = new UserRoles(roles, linked);
                callback.accept(userRoles);
            } else {
                System.err.println("HTTP Request failed with response code: " + responseCode);
                callback.accept(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.accept(null);
        }
    }

    public static class UserRoles {
        private final Set<String> roles;
        private final boolean linked;

        public UserRoles(Set<String> roles, boolean linked) {
            this.roles = roles;
            this.linked = linked;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public boolean isLinked() {
            return linked;
        }
    }
}
