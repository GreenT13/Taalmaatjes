package com.apon.taalmaatjes.frontend.presentation;

import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;

public class NameUtil {

    public static String getStudentName(StudentReturn studentReturn) {
        String studentName = "";

        if (studentReturn.getFirstName() != null) {
            studentName += studentReturn.getFirstName() + " ";
        }

        if (studentReturn.getInsertion() != null) {
            studentName += studentReturn.getInsertion() + " ";
        }

        if (studentReturn.getLastName() != null) {
            studentName += studentReturn.getLastName();
        }

        return studentName;
    }

    public static String getVolunteerName(VolunteerReturn volunteerReturn) {
        if (volunteerReturn == null) {
            return null;
        }

        String volunteerName = "";

        if (volunteerReturn.getFirstName() != null) {
            volunteerName += volunteerReturn.getFirstName() + " ";
        }

        if (volunteerReturn.getInsertion() != null) {
            volunteerName += volunteerReturn.getInsertion() + " ";
        }

        if (volunteerReturn.getLastName() != null) {
            volunteerName += volunteerReturn.getLastName();
        }

        // Last name is always filled.
        return volunteerName;
    }
}
