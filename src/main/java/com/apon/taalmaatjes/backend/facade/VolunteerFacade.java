package com.apon.taalmaatjes.backend.facade;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerInstanceMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;

import javax.annotation.Nonnull;
import java.util.List;

public class VolunteerFacade {
    protected Context context;
    protected VolunteerMyDao volunteerMyDao;
    protected VolunteerInstanceMyDao volunteerInstanceMyDao;

    public VolunteerFacade (Context context) {
        this.context = context;
        volunteerMyDao = new VolunteerMyDao(context.getConfiguration());
        volunteerInstanceMyDao = new VolunteerInstanceMyDao(context.getConfiguration());
    }

    /**
     * Add a volunteer to the database.
     * @param volunteerPojo
     * @return
     */
    public Result addVolunteer(@Nonnull VolunteerPojo volunteerPojo) {
        if (volunteerPojo == null) {
            return new Result("Cannot enter a null pojo.");
        }

        if (volunteerPojo.getLastname() == null) {
            return new Result("Last name must be filled.");
        }

        volunteerMyDao.insert(volunteerPojo);
        if (!context.commit()) {
            return new Result("Could not save the volunteer.");
        }

        return new Result();
    }

    /**
     * Update a volunteer in the database.
     * @param volunteerPojo
     * @return
     */
    public Result updateVolunteer(@Nonnull VolunteerPojo volunteerPojo) {
        if (volunteerPojo == null) {
            return new Result("Cannot enter a null pojo.");
        }

        if (volunteerPojo.getLastname() == null) {
            return new Result("Last name must be filled.");
        }

        if (volunteerPojo.getVolunteerid() == null) {
            return new Result("Cannot update a volunteer without id.");
        }

        volunteerMyDao.update(volunteerPojo);
        if (!context.commit()) {
            return new Result("Could not update the volunteer.");
        }

        return new Result();
    }

    /**
     * Retrieve a volunteer from the database.
     * @param volunteerId
     * @return
     */
    public VolunteerPojo getVolunteer(int volunteerId) {
        return volunteerMyDao.fetchOneByVolunteerid(volunteerId);
    }

    public List<VolunteerPojo> get50MostRecent() {
        return volunteerMyDao.fetch50MostRecent();
    }

    public List<VolunteerinstancePojo> getVolunteerInstanceInOrder(int volunteerId) {
        return volunteerInstanceMyDao.getInstanceForVolunteer(volunteerId, false);
    }

}
