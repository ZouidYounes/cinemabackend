package com.unez.cinema.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.unez.cinema.entities.Film;
import com.unez.cinema.entities.Projection;


@RepositoryRestResource
@CrossOrigin("http://localhost:4200")
public interface ProjectionRepository extends JpaRepository<Projection, Long>{
	public Page<Projection> findByFilm(Film film, Pageable pagaeable);

}
