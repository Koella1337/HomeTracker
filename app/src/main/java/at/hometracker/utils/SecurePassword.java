package at.hometracker.utils;

public class SecurePassword {
    public final String salt;
    public final String hashedPw;

    public SecurePassword(String salt, String hashedPw) {
        this.salt = salt;
        this.hashedPw = hashedPw;
    }

    @Override
    public String toString() {
        return "SecurePassword{" +
                "salt='" + salt + '\'' +
                ", hashedPw='" + hashedPw + '\'' +
                '}';
    }
}
