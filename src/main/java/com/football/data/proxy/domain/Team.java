package com.football.data.proxy.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@Data
@EqualsAndHashCode(exclude = { "competitions", "players" })
@ToString(exclude = { "competitions"})
public class Team implements Serializable {

	@Id
	private Long id;

	public String name;
	public String tla;
	public String shortName;
	public String areaName;
	public String email;

	@ManyToMany(mappedBy = "teams")
	private Set<Competition> competitions = new HashSet<Competition>();
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Player> players = new HashSet<Player>();

}