/*
 * This file is generated by jOOQ.
*/
package com.apon.taalmaatjes.backend.database.generated.tables.pojos;


import java.io.Serializable;
import java.sql.Date;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StudentPojo implements Serializable {

    private static final long serialVersionUID = 2051278094;

    private Integer studentid;
    private String  externalidentifier;
    private String  firstname;
    private String  insertion;
    private String  lastname;
    private String  sex;
    private Date    dateofbirth;
    private String  groupidentification;
    private Boolean hasquit;

    public StudentPojo() {}

    public StudentPojo(StudentPojo value) {
        this.studentid = value.studentid;
        this.externalidentifier = value.externalidentifier;
        this.firstname = value.firstname;
        this.insertion = value.insertion;
        this.lastname = value.lastname;
        this.sex = value.sex;
        this.dateofbirth = value.dateofbirth;
        this.groupidentification = value.groupidentification;
        this.hasquit = value.hasquit;
    }

    public StudentPojo(
        Integer studentid,
        String  externalidentifier,
        String  firstname,
        String  insertion,
        String  lastname,
        String  sex,
        Date    dateofbirth,
        String  groupidentification,
        Boolean hasquit
    ) {
        this.studentid = studentid;
        this.externalidentifier = externalidentifier;
        this.firstname = firstname;
        this.insertion = insertion;
        this.lastname = lastname;
        this.sex = sex;
        this.dateofbirth = dateofbirth;
        this.groupidentification = groupidentification;
        this.hasquit = hasquit;
    }

    public Integer getStudentid() {
        return this.studentid;
    }

    public void setStudentid(Integer studentid) {
        this.studentid = studentid;
    }

    public String getExternalidentifier() {
        return this.externalidentifier;
    }

    public void setExternalidentifier(String externalidentifier) {
        this.externalidentifier = externalidentifier;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getInsertion() {
        return this.insertion;
    }

    public void setInsertion(String insertion) {
        this.insertion = insertion;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getDateofbirth() {
        return this.dateofbirth;
    }

    public void setDateofbirth(Date dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getGroupidentification() {
        return this.groupidentification;
    }

    public void setGroupidentification(String groupidentification) {
        this.groupidentification = groupidentification;
    }

    public Boolean getHasquit() {
        return this.hasquit;
    }

    public void setHasquit(Boolean hasquit) {
        this.hasquit = hasquit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("StudentPojo (");

        sb.append(studentid);
        sb.append(", ").append(externalidentifier);
        sb.append(", ").append(firstname);
        sb.append(", ").append(insertion);
        sb.append(", ").append(lastname);
        sb.append(", ").append(sex);
        sb.append(", ").append(dateofbirth);
        sb.append(", ").append(groupidentification);
        sb.append(", ").append(hasquit);

        sb.append(")");
        return sb.toString();
    }
}
