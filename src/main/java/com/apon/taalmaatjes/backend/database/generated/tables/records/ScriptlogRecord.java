/*
 * This file is generated by jOOQ.
*/
package com.apon.taalmaatjes.backend.database.generated.tables.records;


import com.apon.taalmaatjes.backend.database.generated.tables.Scriptlog;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
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
public class ScriptlogRecord extends UpdatableRecordImpl<ScriptlogRecord> implements Record4<String, Timestamp, Timestamp, Boolean> {

    private static final long serialVersionUID = 695404854;

    /**
     * Setter for <code>PUBLIC.SCRIPTLOG.SCRIPTNAME</code>.
     */
    public void setScriptname(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>PUBLIC.SCRIPTLOG.SCRIPTNAME</code>.
     */
    public String getScriptname() {
        return (String) get(0);
    }

    /**
     * Setter for <code>PUBLIC.SCRIPTLOG.TSSTARTED</code>.
     */
    public void setTsstarted(Timestamp value) {
        set(1, value);
    }

    /**
     * Getter for <code>PUBLIC.SCRIPTLOG.TSSTARTED</code>.
     */
    public Timestamp getTsstarted() {
        return (Timestamp) get(1);
    }

    /**
     * Setter for <code>PUBLIC.SCRIPTLOG.TSFINISHED</code>.
     */
    public void setTsfinished(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>PUBLIC.SCRIPTLOG.TSFINISHED</code>.
     */
    public Timestamp getTsfinished() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>PUBLIC.SCRIPTLOG.ISCOMPLETED</code>.
     */
    public void setIscompleted(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>PUBLIC.SCRIPTLOG.ISCOMPLETED</code>.
     */
    public Boolean getIscompleted() {
        return (Boolean) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<String, Timestamp, Timestamp, Boolean> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<String, Timestamp, Timestamp, Boolean> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Scriptlog.SCRIPTLOG.SCRIPTNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field2() {
        return Scriptlog.SCRIPTLOG.TSSTARTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return Scriptlog.SCRIPTLOG.TSFINISHED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field4() {
        return Scriptlog.SCRIPTLOG.ISCOMPLETED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getScriptname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component2() {
        return getTsstarted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component3() {
        return getTsfinished();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component4() {
        return getIscompleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getScriptname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value2() {
        return getTsstarted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value3() {
        return getTsfinished();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value4() {
        return getIscompleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptlogRecord value1(String value) {
        setScriptname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptlogRecord value2(Timestamp value) {
        setTsstarted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptlogRecord value3(Timestamp value) {
        setTsfinished(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptlogRecord value4(Boolean value) {
        setIscompleted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptlogRecord values(String value1, Timestamp value2, Timestamp value3, Boolean value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ScriptlogRecord
     */
    public ScriptlogRecord() {
        super(Scriptlog.SCRIPTLOG);
    }

    /**
     * Create a detached, initialised ScriptlogRecord
     */
    public ScriptlogRecord(String scriptname, Timestamp tsstarted, Timestamp tsfinished, Boolean iscompleted) {
        super(Scriptlog.SCRIPTLOG);

        set(0, scriptname);
        set(1, tsstarted);
        set(2, tsfinished);
        set(3, iscompleted);
    }
}
