insert into Volunteer (VOLUNTEERID, EXTERNALIDENTIFIER, FIRSTNAME, INSERTION, LASTNAME, DATEOFBIRTH, PHONENUMBER, EMAIL, HASTRAINING) values
  (1, '1001', 'Rico', null, 'Apon', '1993-03-26', '06-1111111', 'testmail@test.com', true),
  (2, '1002', 'Alain', 'van', 'Schijndel', '0001-01-01', 'geknummer', 'emailzonderapenstaartje', false);

insert into volunteerinstance (VOLUNTEERID, VOLUNTEERINSTANCEID, EXTERNALIDENTIFIER, DATESTART, DATEEND) values
  (1, 1, '1', '2018-01-01', null),
  (1, 2, '2', '2018-01-01', null),
(1, 3, '3', '2018-01-01', null),
(1, 4, '4', '2018-01-01', null),
(1, 5, '5', '2018-01-01', null),
(1, 6, '6', '2018-01-01', null),
(1, 7, '7', '2018-01-01', null);

insert into STUDENT (STUDENTID, EXTERNALIDENTIFIER, FIRSTNAME, LASTNAME, ISGROUP, ISLOOKINGFORVOLUNTEER) VALUES
  (1, '4001', 'Cursie', 'Taal', false, true),
  (2, '4002', null, 'Groep 5A', true, false);

insert into VOLUNTEERMATCH (VOLUNTEERID, VOLUNTEERMATCHID, EXTERNALIDENTIFIER, STUDENTID, DATESTART, DATEEND) values
  (1, 1, '3001', 1, '2018-01-01', null);