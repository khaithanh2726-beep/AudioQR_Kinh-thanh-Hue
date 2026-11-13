import java.util.Arrays;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

class SmokePassword {
    public static void main(String[] args) {
        var console = System.console();
        if (console == null) {
            throw new IllegalStateException("Run from an interactive terminal");
        }
        char[] password = console.readPassword("Temporary Admin password: ");
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("A password is required");
        }
        try {
            System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder()
                    .encode(java.nio.CharBuffer.wrap(password)));
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
