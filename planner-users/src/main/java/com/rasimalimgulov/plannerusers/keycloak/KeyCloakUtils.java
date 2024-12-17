package com.rasimalimgulov.plannerusers.keycloak;

import com.rasimalimgulov.plannerusers.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeyCloakUtils {
    private static RealmResource realmResource; // доступ к API realm
    private static UsersResource usersResource;   // доступ к API для работы с пользователями
    private static Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @PostConstruct
    public Keycloak initKeyCloak() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS).build();
            realmResource = keycloak.realm(realm);

            usersResource = realmResource.users();
        }
        return keycloak;
    }

    public Response createKeycloakUser(UserDTO user) {
        if (usersResource == null) {
            initKeyCloak(); // Убедитесь, что ресурсы инициализированы
        }
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword()); ////создаем представление пароля

        UserRepresentation kcUser = new UserRepresentation(); //// представление User-а нового
        kcUser.setUsername(user.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        Response response = usersResource.create(kcUser);
        return response;
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }

    public void addRoles(String userId, List<String> roles) {
        List<RoleRepresentation> kcRoles = new ArrayList<>();
        for (String ob : roles) {
            RoleRepresentation roleRP = realmResource.roles().get(ob).toRepresentation();
            kcRoles.add(roleRP);
        }
        UserResource uniqueUserResource = usersResource.get(userId);
        uniqueUserResource.roles().realmLevel().add(kcRoles);
    }

    public void deleteKeyCloakUser(String userId) {
        UserResource userResource = usersResource.get(userId);
        userResource.remove();
    }

    public UserRepresentation findUserKCById(String userId){
        return usersResource.get(userId).toRepresentation();
    }


    public List<UserRepresentation> searchKCUser(String email){
        return usersResource.searchByAttributes(email);
    }

    public void updateKCUser(UserDTO userDTO){
        CredentialRepresentation credentialRepresentation=createPasswordCredentials(userDTO.getPassword());
        UserRepresentation kcUser=new UserRepresentation();
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setUsername(userDTO.getUsername());
        kcUser.setEmail(userDTO.getEmail());

        UserResource uniqueUserRepresentation=usersResource.get(userDTO.getId());
        uniqueUserRepresentation.update(kcUser);
    }
}
