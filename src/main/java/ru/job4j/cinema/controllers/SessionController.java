package ru.job4j.cinema.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.models.Ticket;
import ru.job4j.cinema.models.User;
import ru.job4j.cinema.services.SessionService;
import ru.job4j.cinema.services.TicketService;
import ru.job4j.cinema.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class SessionController {

    private final UserService userService;
    private final SessionService sessionService;
    private final TicketService ticketService;

    public SessionController(UserService userService,
                            SessionService sessionService,
                            TicketService ticketService) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.ticketService = ticketService;
    }

    @GetMapping("/sessions")
    public String sessions(Model model) {
        model.addAttribute("films", sessionService.findAllSessions());
        return "sessions";
    }

    @GetMapping("/index")
    public String index(@ModelAttribute User user,
                        @RequestParam("session.id") int sessionId,
                        @RequestParam(value = "msg", required = false) String msg,
                        Model model) {
        model.addAttribute("film",
                sessionService.findSessionById(sessionId).get());
        model.addAttribute("seats",
                sessionService.mapFreeSeats(ticketService.getMapOccupiedSeats(sessionId)));
        model.addAttribute("message", msg);
        model.addAttribute("sessionId", sessionId);
        return "index";
    }

    @GetMapping("/formInitialData")
    public String createTicket(@ModelAttribute User user,
                               HttpServletRequest req,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        int sessionId = Integer.parseInt(req.getParameter("session.id"));
        int rowId = Integer.parseInt(req.getParameter("row"));
        Optional<User> userInDb = userService.addUser(user);
        User userFromDb = userInDb.get();
        if (validateReqCell(req)) {
            int cellId = Integer.parseInt(req.getParameter("cell"));
            Optional<Ticket> ticketInDb =
                                    ticketService.createTicket(
                                            new Ticket(0,
                                                        sessionId,
                                                        rowId,
                                                        cellId,
                                                        userFromDb.getId()
                                            ));
            if (ticketInDb.isEmpty()) {
                return getRedirectIfTicketIsEmpty(redirectAttributes,
                                                  sessionId,
                                                  rowId,
                                                  cellId,
                                                  userFromDb
                );
            }
            model.addAttribute("user", userFromDb);
            model.addAttribute("film",
                    sessionService.findSessionById(sessionId).get());
            model.addAttribute("ticket", ticketInDb.get());
            return "ticket";
        }
        return getRedirectIfWrongChoiceOfCells(redirectAttributes,
                                               userFromDb,
                                               sessionId
        );
    }

    private boolean validateReqCell(HttpServletRequest req) {
        String[] cells =  req.getParameterValues("cell");
        return cells != null && cells.length == 1;
    }

    private String getRedirectIfTicketIsEmpty(RedirectAttributes redirectAttributes,
                                         int sessionId,
                                         int rowId,
                                         int cellId,
                                         User userFromDb) {
        String msg = String.format(
                "Выбранное место: ряд - [%d], место - [%d] уже заняты! "
                        + "Повторите выбор снова! ", rowId, cellId
        );
        redirectAttributes.addAttribute("username", userFromDb.getUsername());
        redirectAttributes.addAttribute("email", userFromDb.getEmail());
        redirectAttributes.addAttribute("phone", userFromDb.getPhone());
        redirectAttributes.addAttribute("msg", msg);
        redirectAttributes.addAttribute("session.id", sessionId);
        return "redirect:/index";
    }

    private String getRedirectIfWrongChoiceOfCells(RedirectAttributes redirectAttributes,
                                                   User userFromDb,
                                                   int sessionId) {
        String message = "Не выбрано ни одного места или выбрано мест больше "
                + "одного! Повторите выбор.";
        redirectAttributes.addAttribute("username", userFromDb.getUsername());
        redirectAttributes.addAttribute("email", userFromDb.getEmail());
        redirectAttributes.addAttribute("phone", userFromDb.getPhone());
        redirectAttributes.addAttribute("msg", message);
        redirectAttributes.addAttribute("session.id", sessionId);
        return "redirect:/index";
    }
}
