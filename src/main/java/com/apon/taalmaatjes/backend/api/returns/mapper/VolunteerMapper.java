package com.apon.taalmaatjes.backend.api.returns.mapper;

import com.apon.taalmaatjes.backend.api.returns.VolunteerInstanceReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;
import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class VolunteerMapper {
    private VolunteerReturn volunteerReturn;

    public VolunteerMapper() {
        volunteerReturn = new VolunteerReturn();
    }

    public VolunteerReturn getVolunteerReturn() {
        return volunteerReturn;
    }

    public VolunteerMapper(VolunteerReturn volunteerReturn) {
        this.volunteerReturn = volunteerReturn;
    }

    public void setVolunter(VolunteerPojo volunteerPojo) {
        volunteerReturn.setExternalIdentifier(volunteerPojo.getExternalidentifier());
        volunteerReturn.setFirstName(volunteerPojo.getFirstname());
        volunteerReturn.setInsertion(volunteerPojo.getInsertion());
        volunteerReturn.setLastName(volunteerPojo.getLastname());
        volunteerReturn.setDateOfBirth(volunteerPojo.getDateofbirth());
        volunteerReturn.setPhoneNumber(volunteerPojo.getPhonenumber());
        volunteerReturn.setMobilePhoneNumber(volunteerPojo.getMobilephonenumber());
        volunteerReturn.setEmail(volunteerPojo.getEmail());
        volunteerReturn.setHasTraining(volunteerPojo.getHastraining());
        volunteerReturn.setPostalCode(volunteerPojo.getPostalcode());
        volunteerReturn.setStreetname(volunteerPojo.getStreetname());
        volunteerReturn.setHouseNr(volunteerPojo.getHousenr());
    }

    private void addInstance(VolunteerinstancePojo volunteerinstancePojo) {
        VolunteerInstanceReturn volunteerInstanceReturn = new VolunteerInstanceReturn();
        volunteerInstanceReturn.setVolunteerExternalIdentifier(volunteerReturn.getExternalIdentifier());
        volunteerInstanceReturn.setExternalIdentifier(volunteerinstancePojo.getExternalidentifier());
        volunteerInstanceReturn.setDateStart(volunteerinstancePojo.getDatestart());
        volunteerInstanceReturn.setDateEnd(volunteerinstancePojo.getDateend());

        volunteerReturn.getListVolunteerInstance().add(volunteerInstanceReturn);
    }

    public void setInstanceList(List<VolunteerinstancePojo> listVolunteerInstancePojo) {
        List<VolunteerInstanceReturn> listVolunteerInstanceReturn = new ArrayList();
        volunteerReturn.setListVolunteerInstance(listVolunteerInstanceReturn);

        for (VolunteerinstancePojo volunteerinstancePojo : listVolunteerInstancePojo) {
            addInstance(volunteerinstancePojo);

            // If (dateStart <= current_date && (dateEnd == null || current_date <= dateEnd)), we fill values
            if (volunteerinstancePojo.getDatestart().compareTo(DateTimeUtil.getCurrentDate()) <= 0 &&
                    (volunteerinstancePojo.getDateend() == null ||
                        volunteerinstancePojo.getDateend().compareTo(DateTimeUtil.getCurrentDate()) >= 0)) {
                volunteerReturn.setActiveToday(true);
                volunteerReturn.setActiveUntil(volunteerinstancePojo.getDateend());
            }

        }
    }

    private void addMatch(VolunteermatchPojo volunteermatchPojo, StudentMyDao studentMyDao) {
        VolunteerMatchReturn volunteerMatchReturn = new VolunteerMatchReturn();
        volunteerMatchReturn.setVolunteerReturn(volunteerReturn);
        volunteerMatchReturn.setExternalIdentifier(volunteermatchPojo.getExternalidentifier());
        volunteerMatchReturn.setDateStart(volunteermatchPojo.getDatestart());
        volunteerMatchReturn.setDateEnd(volunteermatchPojo.getDateend());

        // Set the student.
        StudentMapper studentMapper = new StudentMapper();
        studentMapper.setStudent(studentMyDao.fetchOneByStudentid(volunteermatchPojo.getStudentid()));
        volunteerMatchReturn.setStudent(studentMapper.getStudentReturn());

        volunteerReturn.getListVolunteerMatch().add(volunteerMatchReturn);
    }

    public void setMatchList(List<VolunteermatchPojo> listVolunteerMatchPojo, StudentMyDao studentMyDao) {
        List<VolunteerMatchReturn> listVolunteerMatchReturn = new ArrayList();
        volunteerReturn.setListVolunteerMatch(listVolunteerMatchReturn);

        for (VolunteermatchPojo volunteermatchPojo : listVolunteerMatchPojo) {
            addMatch(volunteermatchPojo, studentMyDao);
        }


    }

    public VolunteerPojo getPojo(Integer volunteerId) {
        VolunteerPojo volunteerPojo = new VolunteerPojo();
        volunteerPojo.setVolunteerid(volunteerId);
        volunteerPojo.setExternalidentifier(volunteerReturn.getExternalIdentifier());
        volunteerPojo.setFirstname(volunteerReturn.getFirstName());
        volunteerPojo.setInsertion(volunteerReturn.getInsertion());
        volunteerPojo.setLastname(volunteerReturn.getLastName());
        volunteerPojo.setDateofbirth(volunteerReturn.getDateOfBirth());
        volunteerPojo.setPhonenumber(volunteerReturn.getPhoneNumber());
        volunteerPojo.setMobilephonenumber(volunteerReturn.getMobilePhoneNumber());
        volunteerPojo.setEmail(volunteerReturn.getEmail());
        volunteerPojo.setHastraining(volunteerReturn.getHasTraining());
        volunteerPojo.setPostalcode(volunteerReturn.getPostalCode());
        volunteerPojo.setCity(volunteerReturn.getCity());
        volunteerPojo.setStreetname(volunteerReturn.getStreetname());
        volunteerPojo.setHousenr(volunteerReturn.getHouseNr());

        return volunteerPojo;
    }
}
