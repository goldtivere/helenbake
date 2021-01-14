package com.helenbake.helenbake.domain;






import com.helenbake.helenbake.domain.enums.RoleType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "roles")
public class Role extends BaseEntity{

  	@NotNull
	@Enumerated(EnumType.STRING)
  	@Column(name = "role_name", unique = true)
	private RoleType name;
		
	@Column(name = "role_description")
	private String roleDescription;

	public RoleType getName() {
		return name;
	}

	public void setName(RoleType name) {
		this.name = name;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//
//		Role role = (Role) o;
//
//		return role.getId() != null ? id.equals(role.id) : role.id == null;
//	}
//
//	@Override
//	public int hashCode() {
//		return id != null ? id.hashCode() : 0;
//	}
}
