package com.surjo.oauth.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;


/**
 * Created by sanjoy on 8/6/17.
 */


public class Right implements GrantedAuthority {


    private Long id;

    private String name;
    private String description;

    public Right(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String getAuthority() {
        return name;
    }
}