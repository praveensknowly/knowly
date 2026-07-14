package com.knowly.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.knowly.entity.UserProfile;
import com.knowly.repository.ProfileRepository;

import com.knowly.entity.User;
import com.knowly.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public CustomOAuth2UserService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId(); // "google" or "github"

        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = String.valueOf(attributes.get(registrationId.equals("google") ? "sub" : "id"));

        if ("github".equals(registrationId)) {
            if (email == null || email.isBlank()) {
                email = fetchGithubPrimaryEmail(request.getAccessToken().getTokenValue());
            }
            if (name == null || name.isBlank()) {
                name = (String) attributes.get("login");
            }
        }

        if (email == null || email.isBlank()) {
            if ("github".equals(registrationId)) {
                throw new OAuth2AuthenticationException(
                    "GitHub account has no verified email — please verify an email on GitHub and try again.");
            }
            throw new OAuth2AuthenticationException(
                "No email available from " + registrationId + ". Make sure the email scope/permission is granted.");
        }

        attributes.put("email", email); // normalize so principal.getName() == email everywhere

        Optional<User> existing = userRepository.findByEmail(email);
        User user = existing.orElseGet(User::new);
        boolean isNew = existing.isEmpty();

        if (!isNew) {
            // Prevent silent account takeover of LOCAL accounts
            if ("LOCAL".equals(existing.get().getProvider())) {
                throw new OAuth2AuthenticationException(
                    "An account with this email already exists using password login. Please log in with your password instead.");
            }
        } else {
            user.setEmail(email);
            user.setName(name != null ? name : email);
            user.setProvider(registrationId.toUpperCase());
            user.setProviderId(providerId);
            user.setEmailVerified(true); // trusted, provider already verified it
            userRepository.save(user);

            UserProfile profile = new UserProfile();
            profile.setUser(user);
            profileRepository.save(profile);
        }

        return new DefaultOAuth2User(oauth2User.getAuthorities(), attributes, "email");
    }

    private String fetchGithubPrimaryEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.ACCEPT, "application/vnd.github+json");

        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET,
                java.net.URI.create("https://api.github.com/user/emails"));

        List<Map<String, Object>> emails = restTemplate.exchange(request,
                new org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();

        if (emails == null) return null;

        return emails.stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElse(null);
    }
}