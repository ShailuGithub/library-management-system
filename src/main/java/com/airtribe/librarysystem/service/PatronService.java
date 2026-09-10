package com.airtribe.librarysystem.service;

import com.airtribe.librarysystem.exception.PatronNotFoundException;
import com.airtribe.librarysystem.model.Patron;
import com.airtribe.librarysystem.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PatronService {

    private static final Logger LOGGER = Logger.getLogger(PatronService.class.getName());

    private final Map<String, Patron> patrons = new LinkedHashMap<>();

    public Patron registerPatron(String name, String email) {
        Patron patron = new Patron(IdGenerator.getNextPatronId(), name, email);
        patrons.put(patron.getPatronId(), patron);
        LOGGER.info("Registered patron " + patron);
        return patron;
    }

    public Patron findPatron(String patronId) throws PatronNotFoundException {
        Patron patron = patrons.get(patronId);
        if (patron == null) {
            throw new PatronNotFoundException("No patron found with ID " + patronId);
        }
        return patron;
    }

    public Patron updatePatron(String patronId, String name, String email) throws PatronNotFoundException {
        Patron patron = findPatron(patronId);
        if (name != null) {
            patron.setName(name);
        }
        if (email != null) {
            patron.setEmail(email);
        }
        return patron;
    }

    public List<Patron> getAllPatrons() {
        return new ArrayList<>(patrons.values());
    }
}
