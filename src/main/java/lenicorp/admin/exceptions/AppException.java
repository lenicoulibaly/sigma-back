package lenicorp.admin.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter @Setter
public class AppException extends RuntimeException
{
    private String message;

    private List<String> messages;

    public AppException(List<String> messages)
    {
        super(String.join("|", messages));
        this.messages = messages;
    }
    public AppException(String message)
    {
        super(message);
        this.message = message;
        this.messages = Collections.singletonList(message);
    }
}
