package smileksey.quotesapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {

    @NotNull
    @NotEmpty(message = "User's name cannot be empty")
    @Size(min = 2, message = "Name must contain at least 2 characters")
    private String name;
    @NotNull
    @Email(message = "Email is not valid")
    private String email;
    @NotNull
    @NotEmpty(message = "User's password cannot be empty")
    @Size(min = 5, max = 20, message = "Password's length must be between 5 and 20 symbols")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
