package org.breitling.dragon.framework.utils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person
{
    @Id
    @GeneratedValue
    private Integer id;
    private String  firstName;

    public Person()
    {
        id = 0;
        firstName = "";
    }
    
    public Person(final String name)
    {
        this.firstName = name;
    }
    
    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
}
