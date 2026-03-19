// These are the Data Transfer Objects that are received in HTTP request. In this we mention what kind of fields we are expecting in the request.
// We can use various annotations like @NotNull @NotBlank to ensure that the requests are coming in proper way.

package com.skillsync.mentorservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MentorApplyRequest {

//	This annotation ensures that userId is not null and has some value.
    @NotNull(message = "User ID is required")
    private Long userId;
    
// These annotations ensures that Bio is not empty and it's length is between 50 to 100 characters.
    @NotBlank(message = "Bio is required")
    @Size(min = 50, max = 1000, message = "Bio must be between 50 and 1000 characters")
    private String bio;

// Experience should not be null and it must be greater than 0 and less than 50.
    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience seems too high")
    private Integer experience;

    
//    hourlyRate should be present and it's value has to be greater than 0.0 .
    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hourly rate must be positive")
    private Double hourlyRate;

    private List<Long> skillIds;
}
