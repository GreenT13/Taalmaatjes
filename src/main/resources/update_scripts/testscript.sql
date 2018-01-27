insert into Volunteer (VOLUNTEERID, EXTERNALIDENTIFIER, FIRSTNAME, INSERTION, LASTNAME, DATEOFBIRTH, PHONENUMBER, EMAIL, HASTRAINING) values
  (1, '1001', 'Rico', null, 'Apon', '1993-03-26', '06-1111111', 'testmail@test.com', true),
  (2, '1002', 'Alain', 'van', 'Schijndel', '0001-01-01', 'geknummer', 'emailzonderapenstaartje', false);

insert into volunteerinstance (VOLUNTEERID, VOLUNTEERINSTANCEID, EXTERNALIDENTIFIER, DATESTART, DATEEND) values
  (1, 0, '2001', '2017-01-01', '2017-05-04'),
  (1, 1, '2002', '2018-01-01', null);