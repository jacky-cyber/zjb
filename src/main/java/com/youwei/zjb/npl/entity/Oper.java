package com.youwei.zjb.npl.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Oper {
	
	@Id
	public Integer id;
	
	public String text;
	
	public String action;
}
