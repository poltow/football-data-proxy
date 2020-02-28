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
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@Data
@ToString
public class Competition implements Serializable {

	@Id
	private Long id;

	public String code;
	public String name;
	public String areaName;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Team> teams = new HashSet<Team>();
}