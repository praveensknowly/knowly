package com.knowly.controller;



import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import com.knowly.dto.ChatSessionDto;

import com.knowly.dto.HelpRequestDto;

import com.knowly.dto.HelpingSessionDto;

import com.knowly.dto.LearningSessionDto;

import com.knowly.entity.Message;

import com.knowly.entity.User;
import com.knowly.entity.UserProfile;

import com.knowly.enums.MessageType;

import com.knowly.exceptions.UserNotFoundException;

import com.knowly.repository.MessageRepository;

import com.knowly.service.HelpSessionService;

import com.knowly.service.RatingService;

import com.knowly.service.UserService;



@Controller

public class HelpSessionController {

	private static final Logger logger = LoggerFactory.getLogger(HelpSessionController.class);

	private final HelpSessionService helpSessionService;

	private final UserService userService;

	private final RatingService ratingService;

	private final MessageRepository messageRepo;

	@Value("${chat.upload-dir}")
	private String chatUploadDir;



	public HelpSessionController(HelpSessionService helpSessionService, UserService userService, RatingService ratingService, MessageRepository messageRepo) {

		this.helpSessionService = helpSessionService;

		this.userService = userService;

		this.ratingService = ratingService;

		this.messageRepo = messageRepo;

	}

	@PostMapping("/help-request")

	public String sendHelpRequest(HelpRequestDto dto,Authentication auth,Model model) {

		try {

		helpSessionService.save(dto,auth.getName());

		}catch(IllegalArgumentException e) {

			model.addAttribute("message","How can you send Request to yourSelf 😒😒😒");

			return "error";

		}

		return "redirect:/learning";

	}

	@GetMapping("/helping")

	public String help(Authentication auth, Model model) {

		User user = userService.findByEmail(auth.getName());

		List<HelpingSessionDto> sessions = helpSessionService.findForHelper(auth.getName());



		long pendingCount = sessions.stream().filter(s -> "pending".equals(s.getTab())).count();

		long activeCount = sessions.stream().filter(s -> "active".equals(s.getTab())).count();

		long solvedCount = sessions.stream().filter(s -> "solved".equals(s.getTab())).count();



		model.addAttribute("user", user);

		model.addAttribute("sessions", sessions);

		model.addAttribute("pendingCount", pendingCount);

		model.addAttribute("activeCount", activeCount);

		model.addAttribute("solvedCount", solvedCount);

		return "Helping";

	}



	@GetMapping("/learning")

	public String learning(Authentication auth, Model model) {

		User user = userService.findByEmail(auth.getName());

		List<LearningSessionDto> sessions = helpSessionService.findForRequester(auth.getName());



		long pendingCount = sessions.stream().filter(s -> "pending".equals(s.getTab())).count();

		long activeCount = sessions.stream().filter(s -> "active".equals(s.getTab())).count();

		long solvedCount = sessions.stream().filter(s -> "solved".equals(s.getTab())).count();



		model.addAttribute("user", user);

		model.addAttribute("sessions", sessions);

		model.addAttribute("pendingCount", pendingCount);

		model.addAttribute("activeCount", activeCount);

		model.addAttribute("solvedCount", solvedCount);

		return "Learning";

	}

	@GetMapping("/chat/{sessionId}")

	public String openChat(@PathVariable String sessionId,

	                        @RequestParam(required = false) String error,

	                        @RequestParam(required = false) String expired,

	                        Authentication auth, Model model) {

	    ChatSessionDto chat = helpSessionService.getChatSession(sessionId, auth.getName());

	    model.addAttribute("chat", chat);

	    model.addAttribute("error", error);

	    model.addAttribute("expired", expired);

	    return "Chat";

	}

	@PostMapping("/chat/{sessionId}/message")

	public String sendMessage(@PathVariable String sessionId,

							   @RequestParam String message,

							   Authentication auth,

							   Model model) {

		try {

			helpSessionService.postMessage(sessionId, message, auth.getName());

			return "redirect:/chat/" + sessionId;

		} catch (IllegalStateException e) {

			// Session expired

			return "redirect:/chat/" + sessionId + "?expired=true";

		} catch (IllegalArgumentException e) {

			// Invalid input (blank text, etc.)

			return "redirect:/chat/" + sessionId + "?error=true";

		} catch (Exception e) {

			// Unexpected error

			logger.error("Failed to send message for session {}", sessionId, e);

			return "redirect:/chat/" + sessionId + "?error=true";

		}

	}



	@PostMapping("/chat/{sessionId}/complete")

	public String completeSession(@PathVariable String sessionId, Authentication auth, Model model) {

		try {

			helpSessionService.completeSession(sessionId, auth.getName());

			return "redirect:/chat/" + sessionId;

		} catch (IllegalStateException e) {

			return "redirect:/chat/" + sessionId + "?error=true";

		} catch (Exception e) {

			logger.error("Failed to complete session {}", sessionId, e);

			return "redirect:/chat/" + sessionId + "?error=true";

		}

	}



	@PostMapping("/chat/{sessionId}/rate")

	public String rateSession(@PathVariable String sessionId, @RequestParam Integer stars,

			@RequestParam(required = false) String review, Authentication auth, RedirectAttributes redirectAttrs) {

		try {

			ratingService.submitRating(sessionId, stars, review, auth.getName());

		} catch (IllegalStateException | IllegalArgumentException e) {

			redirectAttrs.addFlashAttribute("error", e.getMessage());

		}

		return "redirect:/chat/" + sessionId;

	}



	@PostMapping("/learning/{sessionId}/delete")

	public String deleteSession(@PathVariable String sessionId, Authentication auth, RedirectAttributes redirectAttrs) {

		try {

			helpSessionService.deleteSession(sessionId, auth.getName());

		} catch (UserNotFoundException | IllegalStateException e) {

			redirectAttrs.addFlashAttribute("error", e.getMessage());

		}

		return "redirect:/learning";

	}

	@GetMapping("/chat/{sessionId}/messages/latest")
	@ResponseBody
	public Map<String, Integer> getMessageCount(@PathVariable String sessionId, Authentication auth) {
		ChatSessionDto chat = helpSessionService.getChatSession(sessionId, auth.getName());
		return Map.of("count", chat.getMessages().size());
	}

	@GetMapping("/chat/attachment/{messageId}")
	public ResponseEntity<Resource> getAttachment(@PathVariable String messageId, Principal principal) throws IOException {
		Message message = messageRepo.findById(messageId).orElseThrow();
		UserProfile requester = message.getSession().getRequester();
		UserProfile helper = message.getSession().getHelper();
		UserProfile current = userService.getProfile(principal.getName());

		if (!current.getId().equals(requester.getId()) && !current.getId().equals(helper.getId())) {
			return ResponseEntity.status(403).build();
		}
		if (message.getAttachmentPath() == null) {
			return ResponseEntity.notFound().build();
		}
		if (message.getAttachmentMimeType() == null || message.getAttachmentMimeType().isBlank()) {
			return ResponseEntity.notFound().build();
		}

		Path path = Paths.get(chatUploadDir).resolve(message.getAttachmentPath()).normalize();
		Resource resource = new UrlResource(path.toUri());
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(message.getAttachmentMimeType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + message.getAttachmentOriginalName() + "\"")
				.body(resource);
	}

	@PostMapping("/chat/{sessionId}/message/attachment")
	public String sendAttachment(@PathVariable String sessionId,
							  @RequestParam("file") MultipartFile file,
							  @RequestParam("type") MessageType type,
							  @RequestParam(value = "caption", required = false) String caption,
							  Principal principal) {
		try {
			helpSessionService.sendAttachmentMessage(sessionId, principal.getName(), file, type, caption);
			return "redirect:/chat/" + sessionId;
		} catch (IllegalStateException e) {
			return "redirect:/chat/" + sessionId + "?expired=true";
		} catch (IllegalArgumentException e) {
			return "redirect:/chat/" + sessionId + "?error=true";
		} catch (Exception e) {
			logger.error("Failed to send attachment for session {}", sessionId, e);
			return "redirect:/chat/" + sessionId + "?error=true";
		}
	}


}

