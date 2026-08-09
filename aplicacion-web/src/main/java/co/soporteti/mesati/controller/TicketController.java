package co.soporteti.mesati.controller;

import co.soporteti.mesati.model.Ticket;
import co.soporteti.mesati.exception.TicketNotFoundException;
import co.soporteti.mesati.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("tickets", service.search(search));
        model.addAttribute("search", search == null ? "" : search);
        return "tickets/list";
    }

    @GetMapping("/nuevo")
    public String newTicket(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("action", "/tickets/guardar");
        model.addAttribute("pageTitle", "Nuevo ticket");
        return "tickets/form";
    }

    @PostMapping("/guardar")
    public String save(@Valid @ModelAttribute("ticket") Ticket ticket, BindingResult result,
                       Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("action", "/tickets/guardar");
            model.addAttribute("pageTitle", "Nuevo ticket");
            return "tickets/form";
        }
        service.create(ticket);
        redirectAttributes.addFlashAttribute("success", "Ticket creado correctamente.");
        return "redirect:/tickets";
    }

    @GetMapping("/editar/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", service.findById(id));
        model.addAttribute("action", "/tickets/actualizar/" + id);
        model.addAttribute("pageTitle", "Editar ticket");
        return "tickets/form";
    }

    @PostMapping("/actualizar/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("ticket") Ticket ticket,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("action", "/tickets/actualizar/" + id);
            model.addAttribute("pageTitle", "Editar ticket");
            return "tickets/form";
        }
        if (!service.update(id, ticket)) {
            throw new TicketNotFoundException(id);
        }
        redirectAttributes.addFlashAttribute("success", "Ticket actualizado correctamente.");
        return "redirect:/tickets";
    }

    @PostMapping("/eliminar/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!service.delete(id)) {
            throw new TicketNotFoundException(id);
        }
        redirectAttributes.addFlashAttribute("success", "Ticket eliminado correctamente.");
        return "redirect:/tickets";
    }
}
