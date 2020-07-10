package com.unez.cinema.dao;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.unez.cinema.entities.Cinema;
import com.unez.cinema.entities.Ville;

@RepositoryRestResource
@CrossOrigin("http://localhost:4200")
public interface CinemaRepository extends JpaRepository<Cinema, Long>{
	public Page<Cinema> findByVilleAndNameContains(Ville ville, String keyword, Pageable pageable);
}
