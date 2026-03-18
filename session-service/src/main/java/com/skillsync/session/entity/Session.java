package com.skillsync.session.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//marks class as JPA Entity (mandatory)
@Entity				


/*
 * 		set table name. (Optional - but best practice)
		If not used class name will be db table name
 * 
 * */
@Table(name = "sessions")		

//Lombok Annotations -> Reduce boilerplate code

/*
 * We can combine @Getter and @Setter + @toString + @equals + @hashCode => @Data
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

	@Id		//Primary-key
	
	/*
	 * id is created by database (MySql/PostgreSql)
	 * 
	 * SEQUENCE - if Oracle, PostgreSql optimized
	 * 
	 * */
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;
	
	/*
	 * name - explicitly mention column name
	 * constraint -> not null -> nullable = false
	 * @Column is optional but for clear schema we have to use 
	 * 
	 * */
	@Column(name = "mentor_id", nullable = false)
	private Long mentorId;
	
	@Column(name = "learner_id", nullable = false)
	private Long learnerId;
	
	@Column(name = "session_date")
	private LocalDateTime sessionDate;
	
	/*
	 * since we are using Enum for status, default without @Enumerated it is ordinal.
	 * Dangerous if we add any new type
	 * 
	 * */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private SessionStatus status;
		
	/*
	 * Automatically sets timestamp on insert
	 * 
	 * */
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	
	
	
}
