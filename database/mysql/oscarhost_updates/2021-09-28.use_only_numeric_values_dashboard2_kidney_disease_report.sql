UPDATE indicatorTemplate SET template =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<indicatorTemplateXML>
	<author>OSCAR BC</author>
	<uid></uid>
	<heading>
		<category>CDM</category>
		<subCategory>Chronic Kidney Disease</subCategory>
		<name>Consider Chronic Kidney Disease</name>
		<definition>
			Patients with chronic kidney disease dx in 2+ bills or 2+ visit encounters within last 3 years or eGFR &lt;60 or ACR &gt;=3
		</definition>
		<framework>DoBC CPQI PSP Panel</framework>
		<frameworkVersion>08-02-2017</frameworkVersion>
		<notes>
		  Created for Doctors of BC
		  i) Chronic Kidney Disease dx in 2+ bills: \"Patient Status: Active
AND does not have a Current Problem List/Profile item code of 90688005, 709044004 OR starting with 585
AND with >= 2 Bills with the ICD9 diagnosis code starting with 585 in the last 3 years\"
		  ii) Chronic Kidney Disease dx in 2+ visits: \"Patient Status: Active
AND does not have a Current Problem List/Profile item code of 90688005, 709044004 OR starting with 585
AND with >= 2 Visit Encounters with Snomed diagnosis code 90688005, 709044004 or the ICD9 diagnosis code starting with 585 in the last 3 years\"
		  iii) eGFR &lt;60 or ACR &gt;=3: \"Patient Status: Active
AND does not have a Current Problem List/Profile item code of 90688005, 709044004 OR starting with 585
AND with last eGFR (estimated Glomerular Filtration Rate) lab result &lt;60 OR a last ACR (Albumin:creatinine ratio) lab result &gt;=3 OR  2 or more eGFR &lt;60 or ACR &gt;=3 in the last 2 years\"
		</notes>
	</heading>
	<indicatorQuery>
		<version>03-13-2018</version>
		<params>
			<parameter id=\"provider\" name=\"Provider Number\" value=\"loggedInProvider\" />
			<parameter id=\"active\" name=\"Active Patients\" value=\"'AC'\" />
			<parameter id=\"dxcodesICD\" name=\"Dx Codes (ICD)\" value=\"585\" />
                        <parameter id=\"dxcodesSysICD\" name=\"DX CodeSystem (ICD)\" value=\"'icd9'\" />
                        <parameter id=\"dxcodesSnoMed\" name=\"Dx Codes (SnoMed)\" value=\"90688005,709044004\" />
                        <parameter id=\"dxcodesSysSnoMed\" name=\"Dx CodeSystem (SnoMed)\" value=\"'snomed%'\" />
			<parameter id=\"billingCode\" name=\"ICD9 Billing Code\" value=\"585\" />
			<parameter id=\"numOfBills\" name=\"Number of ICD9 585 Bills\" value=\"2\" />
			<parameter id=\"numOfVisits\" name=\"Number of Visit Encounters coded 585/ICD9\" value=\"2\" />
			<parameter id=\"obs1Name\" name=\"Obs 1 Name\" value=\"'EGFR'\" />
			<parameter id=\"obs1Result\" name=\"Obs 1 Result\" value=\"60\" />
			<parameter id=\"obs2Name\" name=\"Obs 2 Name\" value=\"'ACR'\" />
			<parameter id=\"obs2Result\" name=\"Obs 2 Result\" value=\"3\" />
			<parameter id=\"numOfObs\" name=\"Number of measurements\" value=\"2\" />
		</params>
		<range>
			<lowerLimit id=\"billingdate\" label=\"From Date\" name=\"Date\" value=\"DATE_SUB( NOW(), INTERVAL 3 YEAR )\" />
			<upperLimit id=\"billingdate\" label=\"Date Today\" name=\"Date\" value=\"NOW()\" />
			<lowerLimit id=\"visitdate\" label=\"From Date\" name=\"Date\" value=\"DATE_SUB( NOW(), INTERVAL 3 YEAR )\" />
			<upperLimit id=\"visitdate\" label=\"Date Today\" name=\"Date\" value=\"NOW()\" />
			<lowerLimit id=\"labdate\" label=\"From Date\" name=\"Date\" value=\"DATE_SUB( NOW(), INTERVAL 2 YEAR )\" />
			<upperLimit id=\"labdate\" label=\"Date Today\" name=\"Date\" value=\"NOW()\" />
		</range>
		<query>
SELECT
  IF ( COUNT(fin.patient) &gt; 0, SUM( CASE WHEN (fin.dx IS NULL AND fin.billcount &gt;= ${numOfBills}) THEN 1 ELSE 0 END ) , 0) AS \"Chronic Kidney Disease dx in ${numOfBills}+ bills\",
  IF ( COUNT(fin.patient) &gt; 0, SUM( CASE WHEN (fin.dx IS NULL AND fin.visitcount &gt;= ${numOfVisits}) THEN 1 ELSE 0 END ) , 0) AS \"Chronic Kidney Disease dx in ${numOfVisits}+ visits\",
  IF ( COUNT(fin.patient) &gt; 0, SUM( CASE WHEN (fin.dx IS NULL AND (fin.result1 &lt; ${obs1Result} OR fin.result2 &gt;= ${obs2Result} OR fin.cntResult1 &gt;= ${numOfObs} OR fin.cntResult2 &gt;= ${numOfObs})) THEN 1 ELSE 0 END ) , 0) AS \"eGFR &lt; ${obs1Result} or ACR &gt;= ${obs2Result}\"
FROM (
  SELECT
    d.demographic_no AS patient,
    BILLING.billcount AS billcount,
    VISIT.visitcount AS visitcount,
    OBS1LAST.result AS result1,
    OBS2LAST.result AS result2,
    OBS1CNT.countOBS AS cntResult1,
    OBS2CNT.countOBS AS cntResult2,
    dxr.dxresearch_code AS dx,
    dxr.coding_system AS dxs
  FROM demographic d
  LEFT JOIN (
    SELECT demographic_no, dxresearch_code, coding_system, count(dxresearch_code)
    FROM dxresearch
    WHERE status='A' AND
    (
      (LEFT(dxresearch_code, 3) IN ('585') AND coding_system = ${dxcodesSysICD}) OR
      (dxresearch_code IN ('90688005','709044004') AND coding_system LIKE ${dxcodesSysSnoMed})
    )
    GROUP BY demographic_no
    ORDER BY demographic_no
  ) dxr
  ON d.demographic_no = dxr.demographic_no
  -- Billed 585 in last 3 years
  LEFT JOIN (
    SELECT b.demographic_no as demographic_no, b.dx_code1 as dxcode1, b.dx_code2 as dxcode2, b.dx_code3 as dxcode3, count(b.demographic_no) as billcount
    FROM dashboard_report_view b
    WHERE ( LEFT(b.dx_code1, 3) IN ('585') OR LEFT(b.dx_code2, 3) IN ('585') OR LEFT(b.dx_code3, 3) IN ('585') )
    AND DATE(b.createdate) > ${lowerLimit.billingdate}
    AND b.demographic_no > 0
    GROUP BY b.demographic_no HAVING COUNT(b.demographic_no) > -1
  ) BILLING
  ON (d.demographic_no = BILLING.demographic_no)
  -- Coded dx during visit encounter in last 3 years
  LEFT JOIN(
    SELECT ASSIGNED.demographic_no as demographic_no, observation_date as visitdate, uuid, count(ASSIGNED.demographic_no) as visitcount
    FROM casemgmt_note note
    INNER JOIN (
      SELECT note_id, CODED.demo_no as demographic_no FROM casemgmt_issue_notes
      INNER JOIN (
        SELECT ci.id as id, ci.update_date, ci.demographic_no demo_no FROM issue i
        INNER JOIN casemgmt_issue ci
        ON i.issue_id = ci.issue_id
        WHERE
	(i.type LIKE ${dxcodesSysICD} AND LEFT(i.code, 3) IN ('585')) OR
	(i.type LIKE ${dxcodesSysSnoMed} AND i.code IN ('90688005','709044004'))
      ) CODED
      ON (casemgmt_issue_notes.id = CODED.id)
      ORDER BY CODED.demo_no
    ) ASSIGNED
    ON note.note_id = ASSIGNED.note_id AND
    -- GET LAST REVISION OF NOTE
    note.note_id= (SELECT MAX(cmn.note_id) FROM casemgmt_note cmn WHERE cmn.uuid = note.uuid) AND
    DATE(note.observation_date) > ${lowerLimit.visitdate}
    GROUP BY demographic_no
    ORDER BY demographic_no
  ) VISIT
  ON (d.demographic_no = VISIT.demographic_no)
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField as result, m.dateObserved AS dateObserved
    FROM measurements m
    INNER JOIN (
      SELECT demographicNo, MAX(dateObserved) AS dateObserved
      FROM measurements
      WHERE type = ${obs1Name}
      GROUP BY demographicNo
    ) mlast
    ON m.demographicNo = mlast.demographicNo
    WHERE m.type = ${obs1Name}
    AND m.dataField REGEXP '^[0-9]+$'
    AND m.dateObserved = mlast.dateObserved
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS1LAST
  ON (d.demographic_no = OBS1LAST.demographic_no)
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField as result, m.dateObserved AS dateObserved
    FROM measurements m
    INNER JOIN (
      SELECT demographicNo, MAX(dateObserved) AS dateObserved
      FROM measurements
      WHERE type = ${obs2Name}
      GROUP BY demographicNo
    ) mlast
    ON m.demographicNo = mlast.demographicNo
    WHERE m.type = ${obs2Name}
    AND m.dateObserved = mlast.dateObserved
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS2LAST
  ON (d.demographic_no = OBS2LAST.demographic_no)
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField, count(m.dataField) as countOBS
    FROM measurements m
    WHERE m.type = ${obs1Name}
    AND m.dataField REGEXP '^[0-9]+$' AND m.dataField &lt; ${obs1Result}
    AND DATE(m.dateObserved) &gt; ${lowerLimit.labdate}
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS1CNT
  ON (d.demographic_no = OBS1CNT.demographic_no)
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField, count(m.dataField) as countOBS
    FROM measurements m
    WHERE m.type = ${obs2Name}
    AND m.dataField &gt;= ${obs2Result}
    AND DATE(m.dateObserved) &gt; ${lowerLimit.labdate}
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS2CNT
  ON (d.demographic_no = OBS2CNT.demographic_no)
  WHERE d.patient_status LIKE ${active}
  AND d.demographic_no &gt; 0
  AND d.provider_no LIKE ${provider}
) fin
		</query>
	</indicatorQuery>
	<drillDownQuery>
		<version>03-13-2018</version>
		<params>
			<parameter id=\"provider\" name=\"Provider Number\" value=\"loggedInProvider\" />
			<parameter id=\"active\" name=\"Active Patients\" value=\"'AC'\" />
			<parameter id=\"dxcodesICD\" name=\"Dx Codes (ICD)\" value=\"585\" />
                        <parameter id=\"dxcodesSysICD\" name=\"DX CodeSystem (ICD)\" value=\"'icd9'\" />
                        <parameter id=\"dxcodesSnoMed\" name=\"Dx Codes (SnoMed)\" value=\"90688005,709044004\" />
                        <parameter id=\"dxcodesSysSnoMed\" name=\"Dx CodeSystem (SnoMed)\" value=\"'snomed%'\" />
			<parameter id=\"billingCode\" name=\"ICD9 Billing Code\" value=\"585\" />
			<parameter id=\"numOfBills\" name=\"Number of ICD9 585 Bills\" value=\"2\" />
			<parameter id=\"numOfVisits\" name=\"Number of Visit Encounters coded 585/ICD9\" value=\"2\" />
			<parameter id=\"obs1Name\" name=\"Obs 1 Name\" value=\"'EGFR'\" />
			<parameter id=\"obs1Result\" name=\"Obs 1 Result\" value=\"60\" />
			<parameter id=\"obs2Name\" name=\"Obs 2 Name\" value=\"'ACR'\" />
			<parameter id=\"obs2Result\" name=\"Obs 2 Result\" value=\"3\" />
			<parameter id=\"numOfObs\" name=\"Number of measurements\" value=\"2\" />
		</params>

		<range>
			<lowerLimit id=\"billingdate\" label=\"From Date\" name=\"Date\" value=\"DATE_SUB( NOW(), INTERVAL 3 YEAR )\" />
			<upperLimit id=\"billingdate\" label=\"Date Today\" name=\"Date\" value=\"NOW()\" />
			<lowerLimit id=\"visitdate\" label=\"From Date\" name=\"Date\" value=\"DATE_SUB( NOW(), INTERVAL 3 YEAR )\" />
			<upperLimit id=\"visitdate\" label=\"Date Today\" name=\"Date\" value=\"NOW()\" />
			<lowerLimit id=\"labdate\" label=\"From Date\" name=\"Date\" value=\"DATE_SUB( NOW(), INTERVAL 2 YEAR )\" />
			<upperLimit id=\"labdate\" label=\"Date Today\" name=\"Date\" value=\"NOW()\" />
		</range>

		<displayColumns>
			<column id=\"demographic\" name=\"d.demographic_no\" title=\"Patient Id\" primary=\"true\" />
			<column id=\"name\" name=\"CONCAT( d.last_name, ', ', d.first_name )\" title=\"Patient Name\" primary=\"false\" />
			<column id=\"dob\" name=\"DATE_FORMAT( CONCAT(d.year_of_birth,'-',d.month_of_birth,'-',d.date_of_birth), '%m-%d-%Y' )\" title=\"Date of Birth\" primary=\"false\" />
			<column id=\"age\" name=\"TIMESTAMPDIFF(YEAR, DATE( CONCAT(d.year_of_birth,'-',d.month_of_birth,'-',d.date_of_birth) ), CURDATE() )\" title=\"Age\" primary=\"false\" />
			<column id=\"billings\" name=\"IFNULL( BILLING.billcount, '0')\" title=\"Billings\" primary=\"false\" />
			<column id=\"visits\" name=\"IFNULL( VISIT.visitcount, '0')\" title=\"Visits\" primary=\"false\" />
			<column id=\"lastOBS1\" name=\"IFNULL( OBS1LAST.result, 'NA')\" title=\"Last EGFR\" primary=\"false\" />
			<column id=\"testDateOBS1\" name=\"IFNULL( DATE_FORMAT( OBS1LAST.dateObserved, '%m-%d-%Y' ), 'NA')\" title=\"EGFR Date (yyyy-mm-dd)\" primary=\"false\" />
			<column id=\"numOBS1s\" name=\"IFNULL( OBS1CNT.countOBS, '0')\" title=\"Num. eGFR &lt;60 Last 2yrs\" primary=\"false\" />
			<column id=\"lastOBS2\" name=\"IFNULL( OBS2LAST.result, 'NA')\" title=\"Last ACR\" primary=\"false\" />
			<column id=\"testDateOBS2\" name=\"IFNULL( DATE_FORMAT( OBS2LAST.dateObserved, '%m-%d-%Y' ), 'NA')\" title=\"ACR Date (yyyy-mm-dd)\" primary=\"false\" />
			<column id=\"numOBS2s\" name=\"IFNULL( OBS2CNT.countOBS, '0')\" title=\"Num. ACR &gt;=3 Last 2yrs\" primary=\"false\" />
		</displayColumns>

		<exportColumns>
			<column id=\"demographic\" name=\"d.demographic_no\" title=\"Patient Id\" primary=\"true\" />
                        <column id=\"firstName\" name=\"d.first_name\" title=\"First Name\" primary=\"false\" />
                        <column id=\"lastName\" name=\"d.last_name\" title=\"Last Name\" primary=\"false\" />
			<column id=\"dob\" name=\"DATE_FORMAT( CONCAT(d.year_of_birth,'-',d.month_of_birth,'-',d.date_of_birth), '%m-%d-%Y' )\" title=\"Date of Birth\" primary=\"false\" />
			<column id=\"age\" name=\"TIMESTAMPDIFF(YEAR, DATE( CONCAT(d.year_of_birth,'-',d.month_of_birth,'-',d.date_of_birth) ), CURDATE() )\" title=\"Age\" primary=\"false\" />
			<column id=\"billings\" name=\"IFNULL( BILLING.billcount, '0')\" title=\"Billings\" primary=\"false\" />
			<column id=\"visits\" name=\"IFNULL( VISIT.visitcount, '0')\" title=\"Visits\" primary=\"false\" />
			<column id=\"lastOBS1\" name=\"IFNULL( OBS1LAST.result, 'NA')\" title=\"Last EGFR\" primary=\"false\" />
			<column id=\"testDateOBS1\" name=\"IFNULL( DATE_FORMAT( OBS1LAST.dateObserved, '%m-%d-%Y' ), 'NA')\" title=\"EGFR Date (yyyy-mm-dd)\" primary=\"false\" />
			<column id=\"numOBS1s\" name=\"IFNULL( OBS1CNT.countOBS, '0')\" title=\"Num. eGFR &lt;60 Last 2yrs\" primary=\"false\" />
			<column id=\"lastOBS2\" name=\"IFNULL( OBS2LAST.result, 'NA')\" title=\"Last ACR\" primary=\"false\" />
			<column id=\"testDateOBS2\" name=\"IFNULL( DATE_FORMAT( OBS2LAST.dateObserved, '%m-%d-%Y' ), 'NA')\" title=\"ACR Date (yyyy-mm-dd)\" primary=\"false\" />
			<column id=\"numOBS2s\" name=\"IFNULL( OBS2CNT.countOBS, '0')\" title=\"Num. ACR &gt;=3 Last 2yrs\" primary=\"false\" />
		</exportColumns>
		<query>
  SELECT *
  FROM demographic d
  LEFT JOIN (
    SELECT demographic_no, dxresearch_code, coding_system, count(dxresearch_code)
    FROM dxresearch
    WHERE status='A' AND
    (
      (LEFT(dxresearch_code, 3) IN ('585') AND coding_system = ${dxcodesSysICD}) OR
      (dxresearch_code IN ('90688005','709044004') AND coding_system LIKE ${dxcodesSysSnoMed})
    )
    GROUP BY demographic_no
    ORDER BY demographic_no
  ) dxr
  ON d.demographic_no = dxr.demographic_no
  -- Billed 585 in last 3 years
  LEFT JOIN (
    SELECT b.demographic_no as demographic_no, b.dx_code1 as dxcode1, b.dx_code2 as dxcode2, b.dx_code3 as dxcode3, count(b.demographic_no) as billcount
    FROM dashboard_report_view b
    WHERE ( LEFT(b.dx_code1, 3) IN ('585') OR LEFT(b.dx_code2, 3) IN ('585') OR LEFT(b.dx_code3, 3) IN ('585') )
    AND DATE(b.createdate) > ${lowerLimit.billingdate}
    AND b.demographic_no > 0
    GROUP BY b.demographic_no HAVING COUNT(b.demographic_no) > -1
  ) BILLING
  ON (d.demographic_no = BILLING.demographic_no)
  -- Coded dx during visit encounter in last 3 years
  LEFT JOIN(
    SELECT ASSIGNED.demographic_no as demographic_no, observation_date as visitdate, uuid, count(ASSIGNED.demographic_no) as visitcount
    FROM casemgmt_note note
    INNER JOIN (
      SELECT note_id, CODED.demo_no as demographic_no FROM casemgmt_issue_notes
      INNER JOIN (
        SELECT ci.id as id, ci.update_date, ci.demographic_no demo_no FROM issue i
        INNER JOIN casemgmt_issue ci
        ON i.issue_id = ci.issue_id
        WHERE
	(i.type LIKE ${dxcodesSysICD} AND LEFT(i.code, 3) IN ('585')) OR
	(i.type LIKE ${dxcodesSysSnoMed} AND i.code IN ('90688005','709044004'))
      ) CODED
      ON (casemgmt_issue_notes.id = CODED.id)
      ORDER BY CODED.demo_no
    ) ASSIGNED
    ON note.note_id = ASSIGNED.note_id AND
    -- GET LAST REVISION OF NOTE
    note.note_id= (SELECT MAX(cmn.note_id) FROM casemgmt_note cmn WHERE cmn.uuid = note.uuid) AND
    DATE(note.observation_date) > ${lowerLimit.visitdate}
    GROUP BY demographic_no
    ORDER BY demographic_no
  ) VISIT
  ON (d.demographic_no = VISIT.demographic_no)
  -- Consider OBS here if needed
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField as result, m.dateObserved AS dateObserved
    FROM measurements m
    INNER JOIN (
      SELECT demographicNo, MAX(dateObserved) AS dateObserved
      FROM measurements
      WHERE type = ${obs1Name}
      GROUP BY demographicNo
    ) mlast
    ON m.demographicNo = mlast.demographicNo
    WHERE m.type = ${obs1Name}
    AND m.dataField REGEXP '^[0-9]+$'
    AND m.dateObserved = mlast.dateObserved
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS1LAST
  ON (d.demographic_no = OBS1LAST.demographic_no)
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField as result, m.dateObserved AS dateObserved
    FROM measurements m
    INNER JOIN (
      SELECT demographicNo, MAX(dateObserved) AS dateObserved
      FROM measurements
      WHERE type = ${obs2Name}
      GROUP BY demographicNo
    ) mlast
    ON m.demographicNo = mlast.demographicNo
    WHERE m.type = ${obs2Name}
    AND m.dateObserved = mlast.dateObserved
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS2LAST
  ON (d.demographic_no = OBS2LAST.demographic_no)
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField, count(m.dataField) as countOBS
    FROM measurements m
    WHERE m.type = ${obs1Name}
    AND m.dataField REGEXP '^[0-9]+$' AND m.dataField &lt; ${obs1Result}
    AND DATE(m.dateObserved) &gt; ${lowerLimit.labdate}
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS1CNT
  ON (d.demographic_no = OBS1CNT.demographic_no)
  LEFT JOIN (
    SELECT m.demographicNo AS demographic_no, m.dataField, count(m.dataField) as countOBS
    FROM measurements m
    WHERE m.type = ${obs2Name}
    AND m.dataField &gt;= ${obs2Result}
    AND DATE(m.dateObserved) &gt; ${lowerLimit.labdate}
    AND m.demographicNo &gt; 0
    GROUP BY m.demographicNo HAVING COUNT(m.demographicNo) &gt; -1
  ) OBS2CNT
  ON (d.demographic_no = OBS2CNT.demographic_no)
  WHERE d.patient_status LIKE ${active}
  AND d.demographic_no > 0
  AND d.provider_no LIKE ${provider}
  AND (
  dxr.dxresearch_code IS NULL AND
  ( (BILLING.billcount IS NOT NULL AND BILLING.billcount &gt;= ${numOfBills}) OR
    (VISIT.visitcount IS NOT NULL AND VISIT.visitcount &gt;= ${numOfVisits}) OR
    ( (OBS1LAST.result IS NOT NULL AND OBS1LAST.result &lt; ${obs1Result}) OR
      (OBS2LAST.result IS NOT NULL AND OBS2LAST.result &gt;= ${obs2Result}) OR
      (OBS1CNT.countOBS IS NOT NULL AND OBS1CNT.countOBS &gt;= ${numOfObs}) OR
      (OBS2CNT.countOBS IS NOT NULL AND OBS2CNT.countOBS &gt;= ${numOfObs})) )
  )
  ORDER BY d.demographic_no
		</query>
	</drillDownQuery>
</indicatorTemplateXML>"
WHERE id = (SELECT id FROM indicatorTemplate WHERE dashboardId=5 AND name LIKE "%Consider Chronic Kidney%");