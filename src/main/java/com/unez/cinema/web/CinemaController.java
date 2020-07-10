package com.unez.cinema.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.unez.cinema.dao.CategorieRepository;
import com.unez.cinema.dao.CinemaRepository;
import com.unez.cinema.dao.FilmRepository;
import com.unez.cinema.dao.PlaceRepository;
import com.unez.cinema.dao.ProjectionRepository;
import com.unez.cinema.dao.SalleRepository;
import com.unez.cinema.dao.SeanceRepository;
import com.unez.cinema.dao.TicketRepository;
import com.unez.cinema.dao.VilleRepository;
import com.unez.cinema.entities.Categorie;
import com.unez.cinema.entities.Cinema;
import com.unez.cinema.entities.Film;
import com.unez.cinema.entities.Place;
import com.unez.cinema.entities.Projection;
import com.unez.cinema.entities.Salle;
import com.unez.cinema.entities.Seance;
import com.unez.cinema.entities.Ticket;
import com.unez.cinema.entities.Ville;
import com.unez.cinema.upload.MyUploadForm;

@Controller
public class CinemaController {

	@Autowired
	private VilleRepository villeRepository;
	@Autowired
	private CinemaRepository cinemaRepository;
	@Autowired
	private SalleRepository salleRepository;
	@Autowired
	private PlaceRepository placeRepository;
	@Autowired
	private SeanceRepository seanceRepository;
	@Autowired
	private FilmRepository filmRepository;
	@Autowired
	private ProjectionRepository projectionRepository;
	@Autowired
	private CategorieRepository categorieRepository;
	@Autowired
	private TicketRepository ticketRepository;

	private final String UPLOAD_DIR = System.getProperty("user.home") + "/cinema/images/";

	@GetMapping("/index")
	public String index() {

		return "index";
	}

	@GetMapping("/villesgest")
	public String villes(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		Page<Ville> villes = villeRepository.findByNameContains(keyword, PageRequest.of(page, size));
		model.addAttribute("villes", villes.getContent());
		model.addAttribute("pages", new int[villes.getTotalPages()]);
		model.addAttribute("currentPage", page);
		model.addAttribute("keyword", keyword);
		return "villesGest";
	}

	@GetMapping("/cinemasgest")
	public String cinemas(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "id", defaultValue = "1") Long ville,
			@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		Ville currentVille = villeRepository.findById(ville).get();
		List<Ville> villes = villeRepository.findAll();
		// Page<Cinema> cinemas = cinemaRepository.findByNameAndVille(keyword,
		// currentVille, PageRequest.of(page,size));
		// Page<Cinema> cinemas = cinemaRepository.findByNameContains(keyword,
		// PageRequest.of(page,size));
		Page<Cinema> cinemas = cinemaRepository.findByVilleAndNameContains(currentVille, keyword,
				PageRequest.of(page, size));
		model.addAttribute("cinemas", cinemas.getContent());
		model.addAttribute("pages", new int[cinemas.getTotalPages()]);
		model.addAttribute("currentPage", page);
		model.addAttribute("keyword", keyword);
		model.addAttribute("ville", ville);
		model.addAttribute("villes", villes);
		model.addAttribute("currentVille", currentVille);
		return "cinemasGest";
	}

	@GetMapping("/sallesgest")
	public String salles(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "cinema", defaultValue = "1") Long cinema,
			@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		Cinema currentCinema = cinemaRepository.findById(cinema).get();
		Page<Salle> salles = salleRepository.findByCinemaAndNameContains(currentCinema, keyword,
				PageRequest.of(page, size));
		model.addAttribute("salles", salles.getContent());
		model.addAttribute("pages", new int[salles.getTotalPages()]);
		model.addAttribute("currentPage", page);
		model.addAttribute("keyword", keyword);
		model.addAttribute("cinema", cinema);
		return "sallesGest";
	}

	@GetMapping("/seancesgest")
	public String seances(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size) {
		List<Seance> seances = seanceRepository.findAll();
		model.addAttribute("seances", seances);
		model.addAttribute("currentPage", page);
		return "seancesGest";
	}

	@GetMapping("/filmsgest")
	public String films(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "3") int size,
			@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		Page<Film> films = filmRepository.findByTitreContains(keyword, PageRequest.of(page, size));
		model.addAttribute("films", films.getContent());
		model.addAttribute("pages", new int[films.getTotalPages()]);
		model.addAttribute("currentPage", page);
		model.addAttribute("keyword", keyword);
		return "filmsGest";
	}

	@GetMapping("/categoriesgest")
	public String categories(Model model) {
		List<Categorie> categories = categorieRepository.findAll();
		model.addAttribute("categories", categories);
		return "categoriesGest";
	}

	@GetMapping("/projectionsgest")
	public String projections(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "film", defaultValue = "1") Long film) {
		List<Ville> villes = villeRepository.findAll();

		Film currentFilm = filmRepository.findById(film).get();
		Page<Projection> projections = projectionRepository.findByFilm(currentFilm, PageRequest.of(page, size));
		model.addAttribute("pages", new int[projections.getTotalPages()]);
		model.addAttribute("currentPage", page);
		model.addAttribute("projections", projections.getContent());
		model.addAttribute("film", film);
		model.addAttribute("villes", villes);
		return "projectionsGest";
	}

	@GetMapping("/ticketsgest")
	public String tickets(Model model) {
		List<Ticket> tickets = ticketRepository.findAll();
		model.addAttribute("tickets", tickets);
		return "ticketsGest";
	}

	@GetMapping("/placesgest")
	public String places(Model model) {
		List<Place> places = placeRepository.findAll();
		model.addAttribute("places", places);
		return "placesGest";
	}

	@GetMapping("/deleteville")
	public String deleteVille(Long id, String keyword) {
		villeRepository.deleteById(id);
		return "redirect:/villesgest?keyword=" + keyword;
	}

	@GetMapping("/deletecinema")
	public String deleteCinema(Long id, String keyword, Long ville) {
		cinemaRepository.deleteById(id);
		return "redirect:/cinemasgest?keyword=" + keyword + "&ville=" + ville;
	}

	@GetMapping("/deletesalle")
	public String deleteSalle(Long id, String keyword, Long cinema) {
		salleRepository.deleteById(id);
		Cinema currentCinema = cinemaRepository.findById(cinema).get();
		currentCinema.setNombreSalles(currentCinema.getNombreSalles() - 1);
		cinemaRepository.save(currentCinema);
		return "redirect:/sallesgest?keyword=" + keyword + "&cinema=" + cinema;
	}

	@GetMapping("/deleteseance")
	public String deleteSeance(Long id) {
		seanceRepository.deleteById(id);
		return "redirect:/seancesgest";
	}

	@GetMapping("/deletefilm")
	public String deleteFilm(Long film) {
		filmRepository.deleteById(film);
		return "redirect:/filmsgest";
	}

	@GetMapping("/deleteplace")
	public String deletePlace(Long id) {
		placeRepository.deleteById(id);
		return "redirect:/placesgest";
	}

	@GetMapping("/deleteprojection")
	public String deleteProjection(Long id, Long film) {
		projectionRepository.deleteById(id);
		return "redirect:/projectionsgest?film=" + film;
	}

	@GetMapping("/deleteticket")
	public String deleteTicket(Long id) {
		ticketRepository.deleteById(id);
		return "redirect:/ticketsgest";
	}

	@GetMapping("/deletecategorie")
	public String deleteCategorie(Long id) {
		categorieRepository.deleteById(id);
		return "redirect:/categoriesgest";
	}

	@GetMapping("/formville")
	public String formVille(Model model) {
		model.addAttribute("ville", new Ville());
		return "formville";
	}

	@PostMapping("/saveville")
	public String saveVille(@Valid Ville ville, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) return "formville";
		villeRepository.save(ville);
		return "redirect:/villesgest";
	}

	@GetMapping("/formcinema")
	public String formCinema(Model model, @RequestParam(name = "ville", defaultValue = "1") Long ville) {
		model.addAttribute("cinema", new Cinema());
		model.addAttribute("ville", ville);
		return "formcinema";
	}

	@GetMapping("/formsalle")
	public String formSalle(Model model, @RequestParam(name = "cinema", defaultValue = "1") Long cinema) {
		model.addAttribute("salle", new Salle());
		model.addAttribute("cinema", cinema);
		return "formsalle";
	}

	@GetMapping("/formfilm")
	public String formFilm(Model model) {
		List<Categorie> categories = categorieRepository.findAll();
		MyUploadForm myUploadForm = new MyUploadForm();
		model.addAttribute("categories", categories);
		model.addAttribute("film", new Film());
		model.addAttribute("myUploadForm",myUploadForm);

		return "formfilm";
	}
	
	@GetMapping("/formprojection")
	public String formProjection(Model model,Long film) {
		List<Salle> salles = salleRepository.findAll();
		List<Seance> seances = seanceRepository.findAll();
		model.addAttribute("salles", salles);
		model.addAttribute("film", film);
		model.addAttribute("seances", seances);
		model.addAttribute("projection", new Projection());

		return "formprojection";
	}

	@GetMapping("/editfilm")
	public String editFilm(Model model, Long film) {
		Film currentFilm = filmRepository.findById(film).get();
		List<Categorie> categories = categorieRepository.findAll();
		MyUploadForm myUploadForm = new MyUploadForm();
		model.addAttribute("categories", categories);
		model.addAttribute("film", currentFilm);
		model.addAttribute("myUploadForm",myUploadForm);
		return "formfilm";
	}
	
	@GetMapping("/editprojection")
	public String editProjection(Model model, Long projection, @RequestParam(name = "film", defaultValue = "1") Long film) {
		Projection currentProjection = projectionRepository.findById(projection).get();
		List<Salle> salles = salleRepository.findAll();
		List<Seance> seances = seanceRepository.findAll();
		model.addAttribute("salles", salles);
		model.addAttribute("film", film);
		model.addAttribute("seances", seances);
		model.addAttribute("projection", currentProjection);

		return "formprojection";
	}

	@PostMapping("/savefilm")
	public String saveFilm(@Valid Film film, HttpServletRequest request, //
			Model model, //
			@ModelAttribute("myUploadForm") MyUploadForm myUploadForm) {
		
		film.setPhoto(this.doUpload(model, myUploadForm));
		filmRepository.save(film);
		return "redirect:/filmsgest";
	}
	
	@PostMapping("/saveprojection")
	public String saveProjection(Projection projection,Model model) {
		projectionRepository.save(projection);
		return "redirect:/projectionsgest?film=" + projection.getFilm().getId();
	}

	@PostMapping("/savecinema")
	public String saveCinema(Cinema cinema) {
		cinemaRepository.save(cinema);
		return "redirect:/cinemasgest?ville=" + cinema.getVille().getId();
	}

	@PostMapping("/savesalle")
	public String saveSalle(Salle salle) {
		salleRepository.save(salle);
		Cinema cinema = cinemaRepository.findById(salle.getCinema().getId()).get();
		cinema.setNombreSalles(cinema.getNombreSalles() + 1);
		cinemaRepository.save(cinema);
		return "redirect:/sallesgest?cinema=" + salle.getCinema().getId();
	}

	private String doUpload(Model model, //
			MyUploadForm myUploadForm) {

		String name = null;

		// Root Directory.
		String uploadRootPath = System.getProperty("user.home") + "/cinema/images";

		File uploadRootDir = new File(uploadRootPath);
		// Create directory if it not exists.
		if (!uploadRootDir.exists()) {
			uploadRootDir.mkdirs();
		}
		MultipartFile[] fileDatas = myUploadForm.getFileDatas();
		//
		List<File> uploadedFiles = new ArrayList<File>();
		List<String> failedFiles = new ArrayList<String>();

		for (MultipartFile fileData : fileDatas) {

			// Client File Name
			name = fileData.getOriginalFilename();

			if (name != null && name.length() > 0) {
				try {
					// Create the file at server
					File serverFile = new File(uploadRootDir + File.separator + name);

					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
					stream.write(fileData.getBytes());
					stream.close();
					//
					uploadedFiles.add(serverFile);

				} catch (Exception e) {

				}

			}
		}

		return name;

	}
}
