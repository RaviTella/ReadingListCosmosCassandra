package com.cosmos.cassandra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class ReadingListController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String reader = "rt";

	private ReadingListRepository readingListRepository;
	private RecommendationRepository  recommendationRepository;
	private String ipaddress;

	@Autowired
	public ReadingListController(ReadingListRepository readingListRepository,RecommendationRepository  recommendationRepository ) {
		this.recommendationRepository=recommendationRepository;
		this.readingListRepository = readingListRepository;
		this.ipaddress = getServerIP();
	}

	@RequestMapping(value = "/readingList", method = RequestMethod.GET)	
	public String readersBooks(Model model) {
		//List<Book> readingList = readingListRepository.findByReader(reader);
		List<Book> readingList = readingListRepository.findAll();
		logger.info("The server IP is : " + this.ipaddress);

		model.addAttribute("serverIP", this.ipaddress);
		if (readingList != null) {
			model.addAttribute("books", readingList);
		}
	        List<Recommendation> recommendations= recommendationRepository.getRecommendations();
		        logger.info("Number of recommendations: "+Integer.toString(recommendations.size()));
        model.addAttribute("recommendations",recommendations);
		return "readingList";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)	
	public String addToReadingList(Book book) {
		book.setReader(reader);
		readingListRepository.save(book);
		logger.info("Added a Book to the reading list: " + book);
		return "redirect:/readingList";
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String deleteFromReadingList(@PathVariable String id) {
		readingListRepository.deleteById(UUID.fromString(id));
		logger.info("Deleted a Book from the reading list: " + "Book ID:"+id);
		return "redirect:/readingList";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public ModelAndView editReadingListView(@PathVariable String id) {
		ModelAndView model = new ModelAndView("editReadingList");
		Book book = readingListRepository.findById(UUID.fromString(id)).orElse(null);
		model.addObject("book", book);
		model.addObject("serverIP", this.ipaddress);
		return model;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String editReadingListItem(Book updatedBook, @RequestParam String action) {

		if (action.equals("update")) {
			Book book = readingListRepository.findById(updatedBook.getId()).orElse(null);
			book.setTitle(updatedBook.getTitle());
			book.setAuthor(updatedBook.getAuthor());
			book.setIsbn(updatedBook.getIsbn());
			book.setDescription(updatedBook.getDescription());
			readingListRepository.save(book);
			logger.info("Edited a Book from the reading list: " + book);

		}

		return "redirect:/readingList";
	}

	private String getServerIP() {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String[] values = ip.toString().split("/");
		return values[1];
	}
}
