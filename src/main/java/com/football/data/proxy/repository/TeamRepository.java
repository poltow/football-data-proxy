package com.football.data.proxy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.football.data.proxy.domain.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
