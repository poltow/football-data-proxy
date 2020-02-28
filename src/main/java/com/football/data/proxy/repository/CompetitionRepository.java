package com.football.data.proxy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.football.data.proxy.domain.Competition;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

	List<Competition> findByCode(String code);

}
