UPDATE indicatorTemplate
SET template = '<?xml version="1.0" encoding="UTF-8"?>
<indicatorTemplateXML>
    <author>OSCAR BC</author>
    <uid></uid>
    <heading>
        <category>General</category>
        <subCategory>Patient Population</subCategory>
        <name>Active Patients</name>
        <definition>% of active patients in the practice last seen &lt; 3 years, 3-5 years, 5+ years and never seen.</definition>
        <framework>DoBC CPQI PSP Panel</framework>
        <frameworkVersion>08-02-2017</frameworkVersion>
        <notes></notes>
    </heading>
    <indicatorQuery>
        <version>03-13-2018</version>
        <params>
            <!-- 
                Use this parameter in the query as ${provider}
                This parameter should be used for fetching patient\'s assigned to a MRP.
                ie: WHERE demographic.provider_no = ${provider}
            -->
            <parameter id="provider" name="provider_no" value="loggedInProvider" />
            <parameter id="pstatus" name="Patient Status" value="\'AC\'" />
        </params>
        <query>
            SELECT SUM(fin.THREE) AS "% Active, Seen within 3 years",
            SUM(fin.FIVETHREE) AS "% Active, Seen 3-5 years ago",
            SUM(fin.FIVEPLUS)  AS "% Active, not seen in 5+ years",
            SUM(fin.NONE)      AS "% Active, no documented encounter"
            FROM (
            SELECT
            IF(DATE_SUB(CURDATE(), INTERVAL 3 YEAR) &lt;= DATE(cmn.observation_date), 1, 0) AS THREE,
            IF(DATE_SUB(CURDATE(), INTERVAL 5 YEAR) &lt;= DATE(cmn.observation_date) AND
            DATE(cmn.observation_date) &lt; DATE_SUB(CURDATE(), INTERVAL 3 YEAR), 1, 0)  AS FIVETHREE,
            IF(DATE(cmn.observation_date) &lt; DATE_SUB(CURDATE(), INTERVAL 5 YEAR), 1, 0)  AS FIVEPLUS,
            IF(cmn.observation_date IS NULL, 1, 0)                                       AS NONE
            FROM demographic dem
            LEFT JOIN (
               SELECT demographic_no, MAX(observation_date) AS observation_date FROM casemgmt_note
               WHERE signed = 1 AND provider_no != \'-1\'
               GROUP BY demographic_no) cmn ON dem.demographic_no = cmn.demographic_no
            WHERE dem.provider_no = \'${provider}\'
            AND dem.patient_status = ${pstatus}
            group by dem.demographic_no
            ) as fin;
        </query>
    </indicatorQuery>
    <drillDownQuery>
        <version>03-13-2018</version>
        <params>
            <parameter id="provider" name="provider_no" value="loggedInProvider" />
            <parameter id="pstatus" name="Patient Status" value="\'AC\'" />
        </params>
        <displayColumns>
            <column id="demographic" name="d.demographic_no" title="Patient Id" primary="true" />
            <column id="name" name="CONCAT( d.last_name, \', \', d.first_name )" title="Patient Name" primary="false" />
            <column id="dob" name="DATE_FORMAT( CONCAT(d.year_of_birth,\'-\',d.month_of_birth,\'-\',d.date_of_birth), \'%m-%d-%Y\' )" title="Date of Birth (mm-dd-yy)" primary="false" />
            <column id="age" name="TIMESTAMPDIFF(YEAR, DATE( CONCAT(d.year_of_birth,\'-\',d.month_of_birth,\'-\',d.date_of_birth) ), CURDATE() )" title="Age" primary="false" />
            <column id="lastEncounter" name="IF(MAX(cmn.observation_date) IS NOT NULL,MAX(cmn.observation_date),\'\')" title="Last Encounter" primary="false" />
            <column id="patientStatus" name="d.patient_status" title="Patient Status" primary="false" />
        </displayColumns>
        <exportColumns>
            <column id="demographic" name="d.demographic_no" title="Patient Id" primary="true" />
            <column id="name" name="CONCAT( d.last_name, \', \', d.first_name )" title="Patient Name" primary="false" />
            <column id="dob" name="DATE_FORMAT( CONCAT(d.year_of_birth,\'-\',d.month_of_birth,\'-\',d.date_of_birth), \'%m-%d-%Y\' )" title="Date of Birth (mm-dd-yy)" primary="false" />
            <column id="age" name="TIMESTAMPDIFF(YEAR, DATE( CONCAT(d.year_of_birth,\'-\',d.month_of_birth,\'-\',d.date_of_birth) ), CURDATE() )" title="Age" primary="false" />
            <column id="lastEncounter" name="IF(MAX(cmn.observation_date) IS NOT NULL,MAX(cmn.observation_date),\'\')" title="Last Encounter" primary="false" />
            <column id="patientStatus" name="d.patient_status" title="Patient Status" primary="false" />
        </exportColumns>
        <query>
            <!-- Drilldown SQL Query here -->
            SELECT
            d.*,
            d.patient_status AS pt_status,
            max(cmn.observation_date) as last_encounter
            FROM demographic d
            LEFT JOIN casemgmt_note cmn
            ON (  d.demographic_no = cmn.demographic_no AND cmn.signed = 1 AND cmn.provider_no != \'-1\'  )
            WHERE d.provider_no = \'${provider}\'
            AND d.patient_status = ${pstatus}
            GROUP BY d.demographic_no
        </query>
    </drillDownQuery>
    <shared>false</shared>
    <sharedMetricSetName>Home - Patient status</sharedMetricSetName>
    <sharedMetricDataId>Status</sharedMetricDataId>
    <sharedMappings>
        <sharedMapping fromLabel="% Active, Seen within 3 years" toLabel="Active, seen within 3 years"/>
        <sharedMapping fromLabel="% Active, Seen 3-5 years ago" toLabel="Active, seen within 3-5 yrs"/>
        <sharedMapping fromLabel="% Active, not seen in 5+ years" toLabel="Active, not seen in 5+ yrs"/>
    </sharedMappings>
</indicatorTemplateXML>'
WHERE name = 'Active Patients' AND
        category = 'General' AND
        subCategory = 'Patient Population' AND
        framework = 'DoBC CPQI PSP Panel' AND
        definition = '% of active patients in the practice last seen < 3 years, 3-5 years, 5+ years and never seen.' AND
        active = 1;