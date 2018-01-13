/*
 * This file is generated by jOOQ.
*/
package com.apon.taalmaatjes.backend.database.generated.tables.records;


import com.apon.taalmaatjes.backend.database.generated.tables.Volunteer;

import java.sql.Date;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;


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
public class VolunteerRecord extends UpdatableRecordImpl<VolunteerRecord> implements Record10<Integer, String, String, String, Date, String, String, String, String, Boolean> {

    private static final long serialVersionUID = -154817506;

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.VOLUNTEERID</code>.
     */
    public void setVolunteerid(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.VOLUNTEERID</code>.
     */
    public Integer getVolunteerid() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.FIRSTNAME</code>.
     */
    public void setFirstname(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.FIRSTNAME</code>.
     */
    public String getFirstname() {
        return (String) get(1);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.INITIALS</code>.
     */
    public void setInitials(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.INITIALS</code>.
     */
    public String getInitials() {
        return (String) get(2);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.LASTNAME</code>.
     */
    public void setLastname(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.LASTNAME</code>.
     */
    public String getLastname() {
        return (String) get(3);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.DATEOFBIRTH</code>.
     */
    public void setDateofbirth(Date value) {
        set(4, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.DATEOFBIRTH</code>.
     */
    public Date getDateofbirth() {
        return (Date) get(4);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.CITY</code>.
     */
    public void setCity(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.CITY</code>.
     */
    public String getCity() {
        return (String) get(5);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.PHONENUMBER</code>.
     */
    public void setPhonenumber(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.PHONENUMBER</code>.
     */
    public String getPhonenumber() {
        return (String) get(6);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.MOBILEPHONENUMBER</code>.
     */
    public void setMobilephonenumber(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.MOBILEPHONENUMBER</code>.
     */
    public String getMobilephonenumber() {
        return (String) get(7);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.EMAIL</code>.
     */
    public void setEmail(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.EMAIL</code>.
     */
    public String getEmail() {
        return (String) get(8);
    }

    /**
     * Setter for <code>PUBLIC.VOLUNTEER.HASTRAINING</code>.
     */
    public void setHastraining(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>PUBLIC.VOLUNTEER.HASTRAINING</code>.
     */
    public Boolean getHastraining() {
        return (Boolean) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, String, String, String, Date, String, String, String, String, Boolean> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, String, String, String, Date, String, String, String, String, Boolean> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Volunteer.VOLUNTEER.VOLUNTEERID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Volunteer.VOLUNTEER.FIRSTNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Volunteer.VOLUNTEER.INITIALS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Volunteer.VOLUNTEER.LASTNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field5() {
        return Volunteer.VOLUNTEER.DATEOFBIRTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Volunteer.VOLUNTEER.CITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Volunteer.VOLUNTEER.PHONENUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Volunteer.VOLUNTEER.MOBILEPHONENUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Volunteer.VOLUNTEER.EMAIL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field10() {
        return Volunteer.VOLUNTEER.HASTRAINING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getVolunteerid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getFirstname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getInitials();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getLastname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date component5() {
        return getDateofbirth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getCity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getPhonenumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getMobilephonenumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component10() {
        return getHastraining();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getVolunteerid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getFirstname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getInitials();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getLastname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value5() {
        return getDateofbirth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getCity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getPhonenumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getMobilephonenumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value10() {
        return getHastraining();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value1(Integer value) {
        setVolunteerid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value2(String value) {
        setFirstname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value3(String value) {
        setInitials(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value4(String value) {
        setLastname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value5(Date value) {
        setDateofbirth(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value6(String value) {
        setCity(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value7(String value) {
        setPhonenumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value8(String value) {
        setMobilephonenumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value9(String value) {
        setEmail(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord value10(Boolean value) {
        setHastraining(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerRecord values(Integer value1, String value2, String value3, String value4, Date value5, String value6, String value7, String value8, String value9, Boolean value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached VolunteerRecord
     */
    public VolunteerRecord() {
        super(Volunteer.VOLUNTEER);
    }

    /**
     * Create a detached, initialised VolunteerRecord
     */
    public VolunteerRecord(Integer volunteerid, String firstname, String initials, String lastname, Date dateofbirth, String city, String phonenumber, String mobilephonenumber, String email, Boolean hastraining) {
        super(Volunteer.VOLUNTEER);

        set(0, volunteerid);
        set(1, firstname);
        set(2, initials);
        set(3, lastname);
        set(4, dateofbirth);
        set(5, city);
        set(6, phonenumber);
        set(7, mobilephonenumber);
        set(8, email);
        set(9, hastraining);
    }
}
