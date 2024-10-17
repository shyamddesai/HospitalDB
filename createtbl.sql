CREATE TABLE hospital
(
        hname VARCHAR(50) NOT NULL
        , haddress VARCHAR(50) NOT NULL
        , company_No INTEGER NOT NULL
        , PRIMARY KEY(hname, haddress)
);

CREATE TABLE personnel
(
        employee_No INTEGER NOT NULL
        , department VARCHAR(50) NOT NULL
        , license_No INTEGER
        , license_expiry DATE
        , hname VARCHAR(50) NOT NULL
        , haddress VARCHAR(50) NOT NULL
        , PRIMARY KEY(employee_No)
        , FOREIGN KEY(hname, haddress) REFERENCES hospital(hname, haddress)
);

CREATE TABLE insurer
(
        iname VARCHAR(50) NOT NULL
        , plan_type VARCHAR(50) NOT NULL
	, PRIMARY KEY(iname)
);

CREATE TABLE patient
(
	phealthcare_No INTEGER NOT NULL
	,pname VARCHAR(50) NOT NULL
	,contact_info VARCHAR(50) NOT NULL
	,sex VARCHAR(10) NOT NULL
	,birthday DATE NOT NULL
	,medical_records VARCHAR(100)
	,current_medication VARCHAR(50)
	,planned_treatment VARCHAR(50)
	,employee_No INTEGER
	,iname VARCHAR(50)
	,PRIMARY KEY(phealthcare_No)
	,FOREIGN KEY(employee_No) REFERENCES personnel(employee_No)
	,FOREIGN KEY(iname) REFERENCES insurer(iname)
);

CREATE TABLE equipment
(
	serial_No INTEGER NOT NULL
	,equipment_type VARCHAR(20) NOT NULL
	,mode_No INTEGER NOT NULL
	,supplier VARCHAR(50) NOT NULL
	,life_expetancy INTEGER NOT NULL
	,hname VARCHAR(50) NOT NULL
	,haddress VARCHAR(50) NOT NULL
	,PRIMARY KEY(serial_No, equipment_type)
	,FOREIGN KEY(hname, haddress) REFERENCES hospital(hname, haddress)
);

CREATE TABLE donor
(
	dhealthcare_No INTEGER NOT NULL
	, blood_type VARCHAR(2) NOT NULL
	, medical_history VARCHAR(100)
	, donation_type VARCHAR(50) NOT NULL
	, daddress VARCHAR(50) NOT NULL
	, PRIMARY KEY(dhealthcare_No)
);

CREATE TABLE pharmacy
(
	phname VARCHAR(50) NOT NULL
	,phaddress VARCHAR(50) NOT NULL
	,PRIMARY KEY(phname, phaddress)
);

CREATE TABLE appointment
(
	phealthcare_No INTEGER NOT NULL
	, adate DATE NOT NULL
	, atime VARCHAR(20) NOT NULL
	, PRIMARY KEY(phealthcare_No, adate, atime)
	, FOREIGN KEY(phealthcare_No) REFERENCES patient(phealthcare_No)
);

CREATE TABLE care
(
	employee_No INTEGER NOT NULL
	, phealthcare_No INTEGER NOT NULL
	, PRIMARY KEY(employee_No, phealthcare_No)
	, FOREIGN KEY(employee_No) REFERENCES personnel(employee_No)
	, FOREIGN KEY(phealthcare_No) REFERENCES patient(phealthcare_No)
);

CREATE TABLE bill 
(
        employee_No INTEGER NOT NULL
        , phealthcare_No INTEGER NOT NULL
	, adate DATE NOT NULL
	, atime VARCHAR(20) NOT NULL
	, appointment_fee DECIMAL(20,2) 
        , PRIMARY KEY(employee_No, phealthcare_No, adate, atime)
        , FOREIGN KEY(employee_No) REFERENCES personnel(employee_No)
        , FOREIGN KEY(phealthcare_No) REFERENCES patient(phealthcare_No)
	, FOREIGN KEY(phealthcare_No, adate, atime) REFERENCES appointment(Phealthcare_No, adate, atime)
);

CREATE TABLE schedule
(
	phealthcare_No INTEGER NOT NULL
	, adate DATE NOT NULL
	, atime VARCHAR(20) NOT NULL
	, employee_No INTEGER NOT NULL
	, PRIMARY KEY(employee_No, phealthcare_No, adate, atime)
        , FOREIGN KEY(employee_No) REFERENCES personnel(employee_No)
        , FOREIGN KEY(phealthcare_No) REFERENCES patient(phealthcare_No)
        , FOREIGN KEY(phealthcare_No, adate, atime) REFERENCES appointment(phealthcare_No, adate, atime)
);

CREATE TABLE conduct
(
        phealthcare_No INTEGER NOT NULL
        , adate DATE NOT NULL
        , atime VARCHAR(20) NOT NULL
        , employee_No INTEGER NOT NULL
	, serial_No INTEGER NOT NULL
	, equipment_type VARCHAR(20) NOT NULL
        , PRIMARY KEY(employee_No, phealthcare_No, adate, atime, serial_No, equipment_type)
);

CREATE TABLE prescribe
(
	phealthcare_No INTEGER NOT NULL
	, employee_No INTEGER NOT NULL
	, phname VARCHAR(50) NOT NULL
	, phaddress VARCHAR(50) NOT NULL
	, medication VARCHAR(100) NOT NULL
	, PRIMARY KEY(phealthcare_No, employee_No, phname, phaddress)
	, FOREIGN KEY(phealthcare_No) REFERENCES patient(phealthcare_No)
	, FOREIGN KEY(employee_No) REFERENCES personnel(employee_No)
	, FOREIGN KEY(phname, phaddress) REFERENCES pharmacy(phname, phaddress)
);

CREATE TABLE donate
(
	dhealthcare_No INTEGER NOT NULL
	, hname VARCHAR(50) NOT NULL
	, haddress VARCHAR(50) NOT NULL
	, PRIMARY KEY(dhealthcare_No, hname, haddress)
	, FOREIGN KEY(dhealthcare_No) REFERENCES donor(dhealthcare_No)
	, FOREIGN KEY(hname, haddress) REFERENCES hospital(hname, haddress)
);

CREATE TABLE share_records 
(
	origin_hname VARCHAR(50) NOT NULL
	, origin_haddress VARCHAR(50) NOT NULL
	, receiving_hname VARCHAR(50) NOT NULL
	, receiving_haddress VARCHAR(50) NOT NULL
	, patient_records VARCHAR(100)
);
