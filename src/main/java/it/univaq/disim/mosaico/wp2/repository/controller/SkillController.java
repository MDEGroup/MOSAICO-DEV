package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import it.univaq.disim.mosaico.wp2.repository.service.SkillService;

/**
 * Controller for Skill operations following MOSAICO taxonomy.
 * 
 * Copyright 2025 Mosaico
 */
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    Logger logger = LoggerFactory.getLogger(SkillController.class);
    
    @Autowired
    private final SkillService skillService;
    
    public SkillController(@Autowired SkillService skillService) {
        this.skillService = skillService;
    }
    
    /**
     * Get all skills.
     */
    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills() {
        logger.info("GET /api/skills");
        List<Skill> skills = skillService.findAll();
        return ResponseEntity.ok(skills);
    }
    
    /**
     * Get skill by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable String id) {
        logger.info("GET /api/skills/{}", id);
        Optional<Skill> skill = skillService.findById(id);
        return skill.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new skill.
     */
    @PostMapping
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        logger.info("POST /api/skills for skill: {}", skill.name());
        Skill savedSkill = skillService.save(skill);
        return ResponseEntity.ok(savedSkill);
    }
    
    /**
     * Update existing skill.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable String id, @RequestBody Skill skill) {
        logger.info("PUT /api/skills/{}", id);
        Optional<Skill> existingSkill = skillService.findById(id);
        if (existingSkill.isPresent()) {
            Skill updatedSkill = skillService.save(skill);
            return ResponseEntity.ok(updatedSkill);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete skill.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable String id) {
        logger.info("DELETE /api/skills/{}", id);
        skillService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Find skills by name.
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Skill>> findSkillsByName(@RequestParam String name) {
        logger.info("GET /api/skills/search/name?name={}", name);
        List<Skill> skills = skillService.findByName(name);
        return ResponseEntity.ok(skills);
    }
    
    /**
     * Find skills by proficiency level.
     */
    @GetMapping("/search/level")
    public ResponseEntity<List<Skill>> findSkillsByLevel(@RequestParam ProficiencyLevel level) {
        logger.info("GET /api/skills/search/level?level={}", level);
        List<Skill> skills = skillService.findByLevel(level);
        return ResponseEntity.ok(skills);
    }
    
    /**
     * Find skills by minimum proficiency level.
     */
    @GetMapping("/search/min-level")
    public ResponseEntity<List<Skill>> findSkillsByMinLevel(@RequestParam ProficiencyLevel minLevel) {
        logger.info("GET /api/skills/search/min-level?minLevel={}", minLevel);
        List<Skill> skills = skillService.findByMinLevel(minLevel);
        return ResponseEntity.ok(skills);
    }
}