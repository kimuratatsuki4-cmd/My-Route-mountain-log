package com.example.mountainlog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mountainlog.entity.MountainNote;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.service.MountainNoteService;

@Controller
@RequestMapping("/mountains")
public class MountainNoteController {
    @Autowired
    private MountainNoteService mountainNoteService;

    @GetMapping("/{mountainId}/notes")
    public String getNotesByMountainId(@PathVariable Integer mountainId, Model model) {

        List<MountainNote> notes = mountainNoteService.getNotesByMountainId(mountainId);
        model.addAttribute("notes", notes);
        return "notes";
    }

    @PostMapping("/{mountainId}/notes/create")
    public String createNote(@PathVariable Integer mountainId, Model model, @RequestParam("title") String title,
            @RequestParam("itemNames") List<String> itemNames, @AuthenticationPrincipal UserDetails userDetails,
            MountainNote mountainNote, RedirectAttributes redirectAttributes) {
        User user = mountainNoteService.getUserByUsername(userDetails.getUsername());
        try {
            mountainNoteService.saveNote(mountainNote, mountainId, title, itemNames, user);
            redirectAttributes.addFlashAttribute("successMesasge", "Note created successfully");
        } catch (Exception e) {
            redirectAttributes.addAttribute("errorMessage", "Failed to create note");
        }
        return "redirect:/mountains/" + mountainId;

    }
}
