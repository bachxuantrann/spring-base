package gem.training_spring.base_app.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestReponse<T> {
    private int statusCode;
    private Object error;
    private Object message;
    private T data;
}
