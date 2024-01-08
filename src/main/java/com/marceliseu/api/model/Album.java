package com.marceliseu.api.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Objects;


@Data
@Document(collection = "albums")
public class Album {
	
	@Id
	private String albumId;

	private String artist;
	
	private String name;
	
	private int year;

}


