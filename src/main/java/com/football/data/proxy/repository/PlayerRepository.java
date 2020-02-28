package com.football.data.proxy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.football.data.proxy.domain.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
