package com.knowly.repository;


import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowly.entity.Language;


@Repository
public interface LanguageRepository extends JpaRepository<Language, String> {
    public Set<Language> findByNameIn(List<String> languages);
}
