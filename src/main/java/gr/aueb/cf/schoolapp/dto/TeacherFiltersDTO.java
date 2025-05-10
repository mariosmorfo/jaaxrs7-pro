package gr.aueb.cf.schoolapp.dto;

//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//public class TeacherFiltersDTO {
//
//    private String firstname;
//    private String lastname;
//    private String vat;
//}

public record TeacherFiltersDTO(String firstname, String lastname, String vat) {}
