insert into STUDENT (STUDENTID, FIRSTNAME, LASTNAME, ISGROUP) VALUES
  (1, 'Cursie', 'Taal', false),
  (2, null, 'Groep 5A', true);

insert into VOLUNTEERMATCH (VOLUNTEERID, VOLUNTEERMATCHID, STUDENTID, DATESTART, DATEEND) values
  (1, 1, 1, '2017-10-02', null),
  (1, 2, 2, '2018-01-01', '2018-01-10'),
  (2, 1, 1, '2017-01-01', '2017-10-01');