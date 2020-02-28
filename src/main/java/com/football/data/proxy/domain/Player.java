package com.football.data.proxy.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@Data
@EqualsAndHashCode(exclude = { "teams" })
@ToString(exclude = { "teams" })
public class Player implements Serializable {

	@Id
	private Long id;

	public String name;
	public String position;
	public Date dateOfBirth;
	public String countryOfBirth;
	public String nationality;

	@ManyToMany(mappedBy = "players")
	private Set<Team> teams = new HashSet<Team>();
}