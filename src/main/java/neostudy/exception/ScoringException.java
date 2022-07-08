package neostudy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScoringException extends Exception {

    private List<String> errorList;

}
