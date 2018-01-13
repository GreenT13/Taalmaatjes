create table Scriptlog (
  scriptName varchar(100) not null,
  tsStarted timestamp not null,
  tsFinished timestamp,
  isCompleted boolean not null,
  constraint "ScLo_PK" primary key (scriptName)
);

insert into Scriptlog values ('20180105_CreateTables', current_timestamp, null, false);

-- Create all kinds of tables.
create table Volunteer (
  volunteerId int not null,
  firstName varchar(100),
  initials varchar(100),
  lastName varchar(100),
  dateOfBirth date,
  city varchar(100),
  phoneNumber varchar(100),
  mobilePhoneNumber varchar(100),
  email varchar(100),
  hasTraining boolean,
  constraint "Volu_PK" primary key (volunteerId)
);

create table VolunteerInstance (
  volunteerId int not null,
  volunteerInstanceId int not null,
  dateStart date,
  dateEnd date,
  constraint "VoIn_PK" primary key (volunteerId, volunteerInstanceId),
  constraint "VoIn_Volu_FK" foreign key (volunteerId) references Volunteer (volunteerId)
);

create table Student (
  studentId int not null,
  firstName varchar(100),
  initials varchar(100),
  lastName varchar(100),
  isLookingForVolunteer boolean,
  isGroup boolean not null,
  constraint "Stud_PK" primary key (studentId)
);

create table VolunteerMatch (
  volunteerId int not null,
  volunteerMatchId int not null,
  studentId int not null,
  dateStart date,
  dateEnd date,
  constraint "VoMa_PK" primary key (volunteerId, volunteerMatchId),
  constraint "VoMa_Volu_FK" foreign key (volunteerId) references Volunteer (volunteerId),
  constraint "VoMa_Stud_FK" foreign key (studentId) references Student (studentId)
);

update Scriptlog set isCompleted = true, tsFinished = current_timestamp where scriptName = '20180105_CreateTables';
