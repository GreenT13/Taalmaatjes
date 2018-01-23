insert into Volunteer (VOLUNTEERID, FIRSTNAME, INSERTION, LASTNAME, DATEOFBIRTH, PHONENUMBER, EMAIL, HASTRAINING) values
  (1, 'Rico', null, 'Apon', '1993-03-26', '06-1111111', 'testmail@test.com', true),
  (2, 'Alain', 'van', 'Schijndel', '0001-01-01', 'geknummer', 'emailzonderapenstaartje', false);

insert into volunteerinstance (VOLUNTEERID, VOLUNTEERINSTANCEID, DATESTART, DATEEND) values
  (1, 0, '2017-01-01', '2017-05-04'),
  (1, 1, '2018-01-01', null);