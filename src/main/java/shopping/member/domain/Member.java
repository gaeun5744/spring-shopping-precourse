package shopping.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    protected Member() {
    }

    public Member(Email email, String encodedPassword) {
        this.email = email.getValue();
        this.password = encodedPassword;
    }

    public boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, password);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
