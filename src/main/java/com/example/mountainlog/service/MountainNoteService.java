package com.example.mountainlog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.MountainNote;
import com.example.mountainlog.entity.NoteItem;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.repository.MountainNoteRepository;
import com.example.mountainlog.repository.MountainRepository;
import com.example.mountainlog.repository.UserRepository;

@Service
public class MountainNoteService {
    @Autowired
    private MountainNoteRepository mountainNoteRepository;
    @Autowired
    private MountainRepository mountainRepository;
    @Autowired
    private UserRepository userRepository;

    public List<MountainNote> getNotesByMountainId(Integer mountainId) {
        return mountainNoteRepository.findByMountain_MountainIdOrderByCreatedAtDesc(mountainId);
    }

    public MountainNote saveNote(MountainNote note, Integer mountainId, String title, List<String> itemNames,
            User user) {
        if (mountainId == null) {
            throw new IllegalArgumentException("Mountain ID cannot be null");
        }
        Mountain mountain = mountainRepository.findById(mountainId)
                .orElseThrow(() -> new RuntimeException("Mountain not found"));

        MountainNote mountainNote = new MountainNote();
        mountainNote.setMountain(mountain);
        mountainNote.setTitle(title);
        mountainNote.setUser(user);

        if (itemNames != null) {
            List<NoteItem> noteItems = new ArrayList<>();
            for (String itemName : itemNames) {
                if (itemName != null && !itemName.trim().isEmpty()) {
                    NoteItem noteItem = new NoteItem();
                    noteItem.setItemName(itemName);
                    noteItem.setMountainNote(mountainNote);
                    noteItems.add(noteItem);
                }
            }
            mountainNote.setNoteItems(noteItems);
        }

        return mountainNoteRepository.save(mountainNote);
    }

    public void deleteNote(Long noteId) {
        if (noteId == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }
        mountainNoteRepository.deleteById(noteId);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}