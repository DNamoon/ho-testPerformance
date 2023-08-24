package com.starter.performance.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member {
  @Id
  @Column(name = "member_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String password;

  private String phoneNumber;

  private String nickname;

  private LocalDateTime registeredDate;

  private LocalDateTime modifiedDate;

  private LocalDateTime withdrawalDate;

  private String permission;

  private boolean emailAuth;

  private boolean sanctionWhether;

  @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
  @Builder.Default
  private List<Review> reviewList = new ArrayList<>();


  @ElementCollection  //'Basic' attribute type should not be a container 에러 나서 붙인 어노테이션
  private List<String> roles;

//  @Override
//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    return this.roles.stream()
//        .map(SimpleGrantedAuthority::new)
//        .collect(Collectors.toList());
//  }
//
//  @Override
//  public String getUsername() {
//    return null;
//  }
//
//  @Override
//  public boolean isAccountNonExpired() {
//    return false;
//  }
//
//  @Override
//  public boolean isAccountNonLocked() {
//    return false;
//  }
//
//  @Override
//  public boolean isCredentialsNonExpired() {
//    return false;
//  }
//
//  @Override
//  public boolean isEnabled() {
//    return false;
//  }
}
