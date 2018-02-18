insert into Volunteer (VOLUNTEERID, EXTERNALIDENTIFIER, FIRSTNAME, INSERTION, LASTNAME, DATEOFBIRTH, PHONENUMBER, EMAIL, dateTraining) values
  (1, '1001', 'Rico', null, 'Apon', '1993-03-26', '06-1111111', 'testmail@test.com', '2018-01-01'),
  (2, '1002', 'Alain', 'van', 'Schijndel', '0001-01-01', 'geknummer', 'emailzonderapenstaartje', null);

insert into volunteerinstance (VOLUNTEERID, VOLUNTEERINSTANCEID, EXTERNALIDENTIFIER, DATESTART, DATEEND) values
  (1, 1, '1', '2018-01-01', null);

insert into STUDENT (STUDENTID, EXTERNALIDENTIFIER, FIRSTNAME, LASTNAME, ISGROUP, ISLOOKINGFORVOLUNTEER) VALUES
  (1, '4001', 'Cursie', 'Taal', false, true),
  (2, '4002', null, 'Groep 5A', true, false);

insert into VOLUNTEERMATCH (VOLUNTEERID, VOLUNTEERMATCHID, EXTERNALIDENTIFIER, STUDENTID, DATESTART, DATEEND) values
  (1, 1, '3001', 1, '2018-01-01', null);