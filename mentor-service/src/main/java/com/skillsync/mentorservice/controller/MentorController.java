package com.skillsync.mentorservice.controller;

import com.skillsync.mentorservice.dto.request.AvailabilityRequest;
import com.skillsync.mentorservice.dto.request.MentorApplyRequest;
import com.skillsync.mentorservice.dto.response.MentorResponse;
import com.skillsync.mentorservice.service.MentorDiscoveryService;
import com.skillsync.mentorservice.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor  // this generates constructor for fields that are either final or marked with @NotNull
public class MentorController {

    private final MentorService mentorService;
    private final MentorDiscoveryService mentorDiscoveryService;

    // POST /mentors/apply
    
    // PostMapping for localhost:abcd/mentors/apply --> At this URL the user can apply itself as mentor by giving the required data.
    // When the data is entered, it will be stroed in the request variable as RequestBody. And then this data is passed to mentorService 
    // method called as applyAsMentor. And that is where the business logic takes place.
    
    
    @PostMapping("/apply")
    public ResponseEntity<MentorResponse> applyAsMentor(@Valid @RequestBody MentorApplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorService.applyAsMentor(request));
    }
    
    

    // GET /mentors

    // URL --> localhost:abcd/mentors 
    // This method is used to search mentors. If there is no filter, it will return all the mentors without any filtration.
    // localhost:abcd/mentors?filters --> This will return the filtered list of mentors based on the filter mentioned.I
    @GetMapping
    public ResponseEntity<List<MentorResponse>> getMentors(
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) Integer minExp,
            @RequestParam(required = false, defaultValue = "rating") String sortBy
    ) {
        if (skillId == null && minRating == null && maxRate == null && minExp == null) {
            return ResponseEntity.ok(mentorService.getAllActiveMentors());
        }
        return ResponseEntity.ok(
                mentorDiscoveryService.searchMentors(skillId, minRating, maxRate, minExp, sortBy));
    }

    
    // GET /mentors/{id}
    // localhost:abcd/mentors/id
    // This method is used to find mentor with his/her id. This method will get the id from the url only as PathVariable.
    
    @GetMapping("/{id}")
    public ResponseEntity<MentorResponse> getMentorById(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }
    

    // PUT /mentors/{id}/availability
    // localhost:abcd/mentors/{id}/availability
    // This method is used to tell at what time the mentor is available. 
    
    @PutMapping("/{id}/availability")
    public ResponseEntity<MentorResponse> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(mentorService.updateAvailability(id, request));
    }
    
    
    
    // GET /{id}/approve
    // localhost;abcd/mentors/{id}/approve
    // This method is used to approve that if user can be converted into a mentor or not. 
    @GetMapping("/{id}/approve")
    public ResponseEntity<MentorResponse> approveMentor(@PathVariable Long id){
    	return ResponseEntity.ok(mentorService.approveMentor(id));
    }
    
}
