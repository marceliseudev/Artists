package com.marceliseu.api.integration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.marceliseu.api.service.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

//@AllArgsConstructor
@Service("firebaseService")
public class FirebaseServiceImpl implements FirebaseService {

    @Value("classpath:projectartists-b5ef8-firebase-adminsdk-gcho5-a359f0ed86.json")
    private Resource firebaseConfig;

    @PostConstruct
    public void init() throws Exception {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseConfig.getInputStream()))
                    .setDatabaseUrl("https://projectartists-b5ef8.firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            throw new ServiceException("Firebase initialization error:" + e.getMessage());
        }
    }

    @Override
    public void addRole(Jwt jwt, String role) throws ServiceException {
        try {
            String userId = jwt.getSubject();
            UserRecord user = FirebaseAuth.getInstance()
                    .getUser(userId);
            Map<String, Object> claims = user.getCustomClaims();
            Map<String, Object> newClaims = new HashMap<>();
            List<String> roles = (List<String>) claims.get("roles");
            if (Objects.isNull(roles)) roles = new ArrayList<>();
            roles.add(role);
            newClaims.put("roles", roles);
            FirebaseAuth.getInstance().setCustomUserClaims(userId, newClaims);
        } catch (Exception e) {
            throw new ServiceException("Could not update firebase role:" + e.getMessage());
        }
    }
}
