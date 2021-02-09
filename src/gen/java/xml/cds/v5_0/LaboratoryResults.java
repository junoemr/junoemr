
package xml.cds.v5_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LaboratoryName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="TestNameReportedByLab" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LabTestCode" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="TestName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AccessionNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Result" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;all&gt;
 *                   &lt;element name="Value"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                         &lt;maxLength value="120"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="UnitOfMeasure" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                         &lt;maxLength value="120"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/all&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ReferenceRange" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;choice&gt;
 *                   &lt;sequence minOccurs="0"&gt;
 *                     &lt;element name="LowLimit" minOccurs="0"&gt;
 *                       &lt;simpleType&gt;
 *                         &lt;restriction base="{cds_dt}text"&gt;
 *                           &lt;maxLength value="1024"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/simpleType&gt;
 *                     &lt;/element&gt;
 *                     &lt;element name="HighLimit" minOccurs="0"&gt;
 *                       &lt;simpleType&gt;
 *                         &lt;restriction base="{cds_dt}text"&gt;
 *                           &lt;maxLength value="1024"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/simpleType&gt;
 *                     &lt;/element&gt;
 *                   &lt;/sequence&gt;
 *                   &lt;element name="ReferenceRangeText" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                         &lt;maxLength value="1024"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/choice&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LabRequisitionDateTime" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="CollectionDateTime" type="{cds_dt}dateTimeFullOrPartial"/&gt;
 *         &lt;element name="ResultReviewer" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence minOccurs="0"&gt;
 *                   &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
 *                   &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="DateTimeResultReviewed" type="{cds_dt}dateTimeFullOrPartial"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ResultNormalAbnormalFlag" type="{cds_dt}resultNormalAbnormalFlag"/&gt;
 *         &lt;element name="TestResultsInformationReportedByTheLab" type="{cds_dt}text32K" minOccurs="0"/&gt;
 *         &lt;element name="NotesFromLab" type="{cds_dt}text32K" minOccurs="0"/&gt;
 *         &lt;element name="PhysiciansNotes" type="{cds_dt}text32K" minOccurs="0"/&gt;
 *         &lt;element name="TestResultStatus" type="{cds_dt}text20" minOccurs="0"/&gt;
 *         &lt;element name="BlockedTestResult" type="{cds_dt}yIndicator" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "laboratoryName",
    "testNameReportedByLab",
    "labTestCode",
    "testName",
    "accessionNumber",
    "result",
    "referenceRange",
    "labRequisitionDateTime",
    "collectionDateTime",
    "resultReviewer",
    "resultNormalAbnormalFlag",
    "testResultsInformationReportedByTheLab",
    "notesFromLab",
    "physiciansNotes",
    "testResultStatus",
    "blockedTestResult"
})
@XmlRootElement(name = "LaboratoryResults", namespace = "cds")
public class LaboratoryResults {

    @XmlElement(name = "LaboratoryName", namespace = "cds", required = true)
    protected String laboratoryName;
    @XmlElement(name = "TestNameReportedByLab", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String testNameReportedByLab;
    @XmlElement(name = "LabTestCode", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String labTestCode;
    @XmlElement(name = "TestName", namespace = "cds")
    protected String testName;
    @XmlElement(name = "AccessionNumber", namespace = "cds")
    protected String accessionNumber;
    @XmlElement(name = "Result", namespace = "cds")
    protected LaboratoryResults.Result result;
    @XmlElement(name = "ReferenceRange", namespace = "cds")
    protected LaboratoryResults.ReferenceRange referenceRange;
    @XmlElement(name = "LabRequisitionDateTime", namespace = "cds")
    protected DateTimeFullOrPartial labRequisitionDateTime;
    @XmlElement(name = "CollectionDateTime", namespace = "cds", required = true)
    protected DateTimeFullOrPartial collectionDateTime;
    @XmlElement(name = "ResultReviewer", namespace = "cds")
    protected List<LaboratoryResults.ResultReviewer> resultReviewer;
    @XmlElement(name = "ResultNormalAbnormalFlag", namespace = "cds", required = true)
    protected ResultNormalAbnormalFlag resultNormalAbnormalFlag;
    @XmlElement(name = "TestResultsInformationReportedByTheLab", namespace = "cds")
    protected String testResultsInformationReportedByTheLab;
    @XmlElement(name = "NotesFromLab", namespace = "cds")
    protected String notesFromLab;
    @XmlElement(name = "PhysiciansNotes", namespace = "cds")
    protected String physiciansNotes;
    @XmlElement(name = "TestResultStatus", namespace = "cds")
    protected String testResultStatus;
    @XmlElement(name = "BlockedTestResult", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected YIndicator blockedTestResult;

    /**
     * Gets the value of the laboratoryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLaboratoryName() {
        return laboratoryName;
    }

    /**
     * Sets the value of the laboratoryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLaboratoryName(String value) {
        this.laboratoryName = value;
    }

    /**
     * Gets the value of the testNameReportedByLab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestNameReportedByLab() {
        return testNameReportedByLab;
    }

    /**
     * Sets the value of the testNameReportedByLab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestNameReportedByLab(String value) {
        this.testNameReportedByLab = value;
    }

    /**
     * Gets the value of the labTestCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabTestCode() {
        return labTestCode;
    }

    /**
     * Sets the value of the labTestCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabTestCode(String value) {
        this.labTestCode = value;
    }

    /**
     * Gets the value of the testName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Sets the value of the testName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestName(String value) {
        this.testName = value;
    }

    /**
     * Gets the value of the accessionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessionNumber() {
        return accessionNumber;
    }

    /**
     * Sets the value of the accessionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessionNumber(String value) {
        this.accessionNumber = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link LaboratoryResults.Result }
     *     
     */
    public LaboratoryResults.Result getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link LaboratoryResults.Result }
     *     
     */
    public void setResult(LaboratoryResults.Result value) {
        this.result = value;
    }

    /**
     * Gets the value of the referenceRange property.
     * 
     * @return
     *     possible object is
     *     {@link LaboratoryResults.ReferenceRange }
     *     
     */
    public LaboratoryResults.ReferenceRange getReferenceRange() {
        return referenceRange;
    }

    /**
     * Sets the value of the referenceRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link LaboratoryResults.ReferenceRange }
     *     
     */
    public void setReferenceRange(LaboratoryResults.ReferenceRange value) {
        this.referenceRange = value;
    }

    /**
     * Gets the value of the labRequisitionDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getLabRequisitionDateTime() {
        return labRequisitionDateTime;
    }

    /**
     * Sets the value of the labRequisitionDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setLabRequisitionDateTime(DateTimeFullOrPartial value) {
        this.labRequisitionDateTime = value;
    }

    /**
     * Gets the value of the collectionDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getCollectionDateTime() {
        return collectionDateTime;
    }

    /**
     * Sets the value of the collectionDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setCollectionDateTime(DateTimeFullOrPartial value) {
        this.collectionDateTime = value;
    }

    /**
     * Gets the value of the resultReviewer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultReviewer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultReviewer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LaboratoryResults.ResultReviewer }
     * 
     * 
     */
    public List<LaboratoryResults.ResultReviewer> getResultReviewer() {
        if (resultReviewer == null) {
            resultReviewer = new ArrayList<LaboratoryResults.ResultReviewer>();
        }
        return this.resultReviewer;
    }

    /**
     * Gets the value of the resultNormalAbnormalFlag property.
     * 
     * @return
     *     possible object is
     *     {@link ResultNormalAbnormalFlag }
     *     
     */
    public ResultNormalAbnormalFlag getResultNormalAbnormalFlag() {
        return resultNormalAbnormalFlag;
    }

    /**
     * Sets the value of the resultNormalAbnormalFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultNormalAbnormalFlag }
     *     
     */
    public void setResultNormalAbnormalFlag(ResultNormalAbnormalFlag value) {
        this.resultNormalAbnormalFlag = value;
    }

    /**
     * Gets the value of the testResultsInformationReportedByTheLab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestResultsInformationReportedByTheLab() {
        return testResultsInformationReportedByTheLab;
    }

    /**
     * Sets the value of the testResultsInformationReportedByTheLab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestResultsInformationReportedByTheLab(String value) {
        this.testResultsInformationReportedByTheLab = value;
    }

    /**
     * Gets the value of the notesFromLab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotesFromLab() {
        return notesFromLab;
    }

    /**
     * Sets the value of the notesFromLab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotesFromLab(String value) {
        this.notesFromLab = value;
    }

    /**
     * Gets the value of the physiciansNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhysiciansNotes() {
        return physiciansNotes;
    }

    /**
     * Sets the value of the physiciansNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhysiciansNotes(String value) {
        this.physiciansNotes = value;
    }

    /**
     * Gets the value of the testResultStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestResultStatus() {
        return testResultStatus;
    }

    /**
     * Sets the value of the testResultStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestResultStatus(String value) {
        this.testResultStatus = value;
    }

    /**
     * Gets the value of the blockedTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link YIndicator }
     *     
     */
    public YIndicator getBlockedTestResult() {
        return blockedTestResult;
    }

    /**
     * Sets the value of the blockedTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link YIndicator }
     *     
     */
    public void setBlockedTestResult(YIndicator value) {
        this.blockedTestResult = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;choice&gt;
     *         &lt;sequence minOccurs="0"&gt;
     *           &lt;element name="LowLimit" minOccurs="0"&gt;
     *             &lt;simpleType&gt;
     *               &lt;restriction base="{cds_dt}text"&gt;
     *                 &lt;maxLength value="1024"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/simpleType&gt;
     *           &lt;/element&gt;
     *           &lt;element name="HighLimit" minOccurs="0"&gt;
     *             &lt;simpleType&gt;
     *               &lt;restriction base="{cds_dt}text"&gt;
     *                 &lt;maxLength value="1024"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/simpleType&gt;
     *           &lt;/element&gt;
     *         &lt;/sequence&gt;
     *         &lt;element name="ReferenceRangeText" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *               &lt;maxLength value="1024"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *       &lt;/choice&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "lowLimit",
        "highLimit",
        "referenceRangeText"
    })
    public static class ReferenceRange {

        @XmlElement(name = "LowLimit", namespace = "cds")
        protected String lowLimit;
        @XmlElement(name = "HighLimit", namespace = "cds")
        protected String highLimit;
        @XmlElement(name = "ReferenceRangeText", namespace = "cds")
        protected String referenceRangeText;

        /**
         * Gets the value of the lowLimit property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLowLimit() {
            return lowLimit;
        }

        /**
         * Sets the value of the lowLimit property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLowLimit(String value) {
            this.lowLimit = value;
        }

        /**
         * Gets the value of the highLimit property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHighLimit() {
            return highLimit;
        }

        /**
         * Sets the value of the highLimit property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHighLimit(String value) {
            this.highLimit = value;
        }

        /**
         * Gets the value of the referenceRangeText property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReferenceRangeText() {
            return referenceRangeText;
        }

        /**
         * Sets the value of the referenceRangeText property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReferenceRangeText(String value) {
            this.referenceRangeText = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;all&gt;
     *         &lt;element name="Value"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *               &lt;maxLength value="120"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="UnitOfMeasure" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *               &lt;maxLength value="120"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *       &lt;/all&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Result {

        @XmlElement(name = "Value", namespace = "cds", required = true)
        protected String value;
        @XmlElement(name = "UnitOfMeasure", namespace = "cds")
        protected String unitOfMeasure;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the unitOfMeasure property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUnitOfMeasure() {
            return unitOfMeasure;
        }

        /**
         * Sets the value of the unitOfMeasure property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUnitOfMeasure(String value) {
            this.unitOfMeasure = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence minOccurs="0"&gt;
     *         &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
     *         &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="DateTimeResultReviewed" type="{cds_dt}dateTimeFullOrPartial"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "ohipPhysicianId",
        "dateTimeResultReviewed"
    })
    public static class ResultReviewer {

        @XmlElement(name = "Name", namespace = "cds")
        protected PersonNameSimple name;
        @XmlElement(name = "OHIPPhysicianId", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ohipPhysicianId;
        @XmlElement(name = "DateTimeResultReviewed", namespace = "cds")
        protected DateTimeFullOrPartial dateTimeResultReviewed;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNameSimple }
         *     
         */
        public PersonNameSimple getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNameSimple }
         *     
         */
        public void setName(PersonNameSimple value) {
            this.name = value;
        }

        /**
         * Gets the value of the ohipPhysicianId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOHIPPhysicianId() {
            return ohipPhysicianId;
        }

        /**
         * Sets the value of the ohipPhysicianId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOHIPPhysicianId(String value) {
            this.ohipPhysicianId = value;
        }

        /**
         * Gets the value of the dateTimeResultReviewed property.
         * 
         * @return
         *     possible object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public DateTimeFullOrPartial getDateTimeResultReviewed() {
            return dateTimeResultReviewed;
        }

        /**
         * Sets the value of the dateTimeResultReviewed property.
         * 
         * @param value
         *     allowed object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public void setDateTimeResultReviewed(DateTimeFullOrPartial value) {
            this.dateTimeResultReviewed = value;
        }

    }

}
