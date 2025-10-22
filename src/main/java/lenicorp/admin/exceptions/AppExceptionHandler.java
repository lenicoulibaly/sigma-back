package lenicorp.admin.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice @RequiredArgsConstructor
public class AppExceptionHandler
{
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException err)
    {
        List<String> errorMessages = new ArrayList<>();
        err.printStackTrace();
        err.getGlobalErrors().forEach(e->{
            String gErr = e.getDefaultMessage();
            if(gErr != null && gErr.contains("::")) errorMessages.add(gErr.split("::")[1]);
            if(gErr != null && !gErr.contains("::")) errorMessages.add(gErr);
        });
        err.getBindingResult().getFieldErrors().forEach(e->errorMessages.add(e.getDefaultMessage()));
        return errorMessages;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<String> handleAppException(AppException err)
    {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(err.getMessage());
        return errorMessages;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<String> handleAuthException(AuthenticationException exception)
    {
        List<String> errorMessages = new ArrayList<>();
        String errMsg = exception instanceof DisabledException ?
                "Votre compte a bien été créé mais n'est pas encore activé.\nPour recevoir un lien d'activation, veillez cliquer sur le lien ci-dessous." :
                exception instanceof LockedException ? "Compte bloqué" :
                exception instanceof InsufficientAuthenticationException ? exception.getMessage() : "Username ou mot de passe incorrect";
        errorMessages.add(errMsg);
        return errorMessages;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleJwtExpirationException(ExpiredJwtException exception)
    {
        return "EXPIRED_TOKEN";
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleAuthException(Exception exception) throws UnknownHostException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stacktrace = sw.toString();
        exception.printStackTrace();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleConstraintViolationException(ConstraintViolationException ex) {
        // Convertir les violations en une liste de messages
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
