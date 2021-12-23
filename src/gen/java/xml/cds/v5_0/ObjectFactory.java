
package xml.cds.v5_0;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the xml.cds.v5_0 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PhoneNumberPhoneNumber_QNAME = new QName("cds_dt", "phoneNumber");
    private final static QName _PhoneNumberExtension_QNAME = new QName("cds_dt", "extension");
    private final static QName _PhoneNumberAreaCode_QNAME = new QName("cds_dt", "areaCode");
    private final static QName _PhoneNumberNumber_QNAME = new QName("cds_dt", "number");
    private final static QName _PhoneNumberExchange_QNAME = new QName("cds_dt", "exchange");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: xml.cds.v5_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Demographics }
     * 
     */
    public Demographics createDemographics() {
        return new Demographics();
    }

    /**
     * Create an instance of {@link MedicationsAndTreatments }
     * 
     */
    public MedicationsAndTreatments createMedicationsAndTreatments() {
        return new MedicationsAndTreatments();
    }

    /**
     * Create an instance of {@link LaboratoryResults }
     * 
     */
    public LaboratoryResults createLaboratoryResults() {
        return new LaboratoryResults();
    }

    /**
     * Create an instance of {@link Appointments }
     * 
     */
    public Appointments createAppointments() {
        return new Appointments();
    }

    /**
     * Create an instance of {@link ClinicalNotes }
     * 
     */
    public ClinicalNotes createClinicalNotes() {
        return new ClinicalNotes();
    }

    /**
     * Create an instance of {@link Reports }
     * 
     */
    public Reports createReports() {
        return new Reports();
    }

    /**
     * Create an instance of {@link Demographics.Enrolment }
     * 
     */
    public Demographics.Enrolment createDemographicsEnrolment() {
        return new Demographics.Enrolment();
    }

    /**
     * Create an instance of {@link Demographics.Enrolment.EnrolmentHistory }
     * 
     */
    public Demographics.Enrolment.EnrolmentHistory createDemographicsEnrolmentEnrolmentHistory() {
        return new Demographics.Enrolment.EnrolmentHistory();
    }

    /**
     * Create an instance of {@link ResidualInformationForAlerts }
     * 
     */
    public ResidualInformationForAlerts createResidualInformationForAlerts() {
        return new ResidualInformationForAlerts();
    }

    /**
     * Create an instance of {@link ResidualInformation }
     * 
     */
    public ResidualInformation createResidualInformation() {
        return new ResidualInformation();
    }

    /**
     * Create an instance of {@link PersonNameStandard }
     * 
     */
    public PersonNameStandard createPersonNameStandard() {
        return new PersonNameStandard();
    }

    /**
     * Create an instance of {@link PersonNameStandard.OtherNames }
     * 
     */
    public PersonNameStandard.OtherNames createPersonNameStandardOtherNames() {
        return new PersonNameStandard.OtherNames();
    }

    /**
     * Create an instance of {@link PersonNameStandard.LegalName }
     * 
     */
    public PersonNameStandard.LegalName createPersonNameStandardLegalName() {
        return new PersonNameStandard.LegalName();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link AddressStructured }
     * 
     */
    public AddressStructured createAddressStructured() {
        return new AddressStructured();
    }

    /**
     * Create an instance of {@link Code }
     * 
     */
    public Code createCode() {
        return new Code();
    }

    /**
     * Create an instance of {@link StandardCoding }
     * 
     */
    public StandardCoding createStandardCoding() {
        return new StandardCoding();
    }

    /**
     * Create an instance of {@link DateFullOrPartial }
     * 
     */
    public DateFullOrPartial createDateFullOrPartial() {
        return new DateFullOrPartial();
    }

    /**
     * Create an instance of {@link DateTimeFullOrPartial }
     * 
     */
    public DateTimeFullOrPartial createDateTimeFullOrPartial() {
        return new DateTimeFullOrPartial();
    }

    /**
     * Create an instance of {@link DrugMeasure }
     * 
     */
    public DrugMeasure createDrugMeasure() {
        return new DrugMeasure();
    }

    /**
     * Create an instance of {@link EnrollmentInfo }
     * 
     */
    public EnrollmentInfo createEnrollmentInfo() {
        return new EnrollmentInfo();
    }

    /**
     * Create an instance of {@link HealthCard }
     * 
     */
    public HealthCard createHealthCard() {
        return new HealthCard();
    }

    /**
     * Create an instance of {@link PersonNameSimple }
     * 
     */
    public PersonNameSimple createPersonNameSimple() {
        return new PersonNameSimple();
    }

    /**
     * Create an instance of {@link PersonNameSimpleWithMiddleName }
     * 
     */
    public PersonNameSimpleWithMiddleName createPersonNameSimpleWithMiddleName() {
        return new PersonNameSimpleWithMiddleName();
    }

    /**
     * Create an instance of {@link PurposeEnumOrPlainText }
     * 
     */
    public PurposeEnumOrPlainText createPurposeEnumOrPlainText() {
        return new PurposeEnumOrPlainText();
    }

    /**
     * Create an instance of {@link PhoneNumber }
     * 
     */
    public PhoneNumber createPhoneNumber() {
        return new PhoneNumber();
    }

    /**
     * Create an instance of {@link PostalZipCode }
     * 
     */
    public PostalZipCode createPostalZipCode() {
        return new PostalZipCode();
    }

    /**
     * Create an instance of {@link ReportContent }
     * 
     */
    public ReportContent createReportContent() {
        return new ReportContent();
    }

    /**
     * Create an instance of {@link ResultNormalAbnormalFlag }
     * 
     */
    public ResultNormalAbnormalFlag createResultNormalAbnormalFlag() {
        return new ResultNormalAbnormalFlag();
    }

    /**
     * Create an instance of {@link YnIndicator }
     * 
     */
    public YnIndicator createYnIndicator() {
        return new YnIndicator();
    }

    /**
     * Create an instance of {@link YnIndicatorAndBlank }
     * 
     */
    public YnIndicatorAndBlank createYnIndicatorAndBlank() {
        return new YnIndicatorAndBlank();
    }

    /**
     * Create an instance of {@link SmokingStatus }
     * 
     */
    public SmokingStatus createSmokingStatus() {
        return new SmokingStatus();
    }

    /**
     * Create an instance of {@link SmokingPacks }
     * 
     */
    public SmokingPacks createSmokingPacks() {
        return new SmokingPacks();
    }

    /**
     * Create an instance of {@link Weight }
     * 
     */
    public Weight createWeight() {
        return new Weight();
    }

    /**
     * Create an instance of {@link Height }
     * 
     */
    public Height createHeight() {
        return new Height();
    }

    /**
     * Create an instance of {@link WaistCircumference }
     * 
     */
    public WaistCircumference createWaistCircumference() {
        return new WaistCircumference();
    }

    /**
     * Create an instance of {@link BloodPressure }
     * 
     */
    public BloodPressure createBloodPressure() {
        return new BloodPressure();
    }

    /**
     * Create an instance of {@link DiabetesComplicationScreening }
     * 
     */
    public DiabetesComplicationScreening createDiabetesComplicationScreening() {
        return new DiabetesComplicationScreening();
    }

    /**
     * Create an instance of {@link DiabetesMotivationalCounselling }
     * 
     */
    public DiabetesMotivationalCounselling createDiabetesMotivationalCounselling() {
        return new DiabetesMotivationalCounselling();
    }

    /**
     * Create an instance of {@link DiabetesSelfManagementCollaborative }
     * 
     */
    public DiabetesSelfManagementCollaborative createDiabetesSelfManagementCollaborative() {
        return new DiabetesSelfManagementCollaborative();
    }

    /**
     * Create an instance of {@link DiabetesSelfManagementChallenges }
     * 
     */
    public DiabetesSelfManagementChallenges createDiabetesSelfManagementChallenges() {
        return new DiabetesSelfManagementChallenges();
    }

    /**
     * Create an instance of {@link DiabetesEducationalSelfManagement }
     * 
     */
    public DiabetesEducationalSelfManagement createDiabetesEducationalSelfManagement() {
        return new DiabetesEducationalSelfManagement();
    }

    /**
     * Create an instance of {@link HypoglycemicEpisodes }
     * 
     */
    public HypoglycemicEpisodes createHypoglycemicEpisodes() {
        return new HypoglycemicEpisodes();
    }

    /**
     * Create an instance of {@link SelfMonitoringBloodGlucose }
     * 
     */
    public SelfMonitoringBloodGlucose createSelfMonitoringBloodGlucose() {
        return new SelfMonitoringBloodGlucose();
    }

    /**
     * Create an instance of {@link DrugCode }
     * 
     */
    public DrugCode createDrugCode() {
        return new DrugCode();
    }

    /**
     * Create an instance of {@link OmdCds }
     * 
     */
    public OmdCds createOmdCds() {
        return new OmdCds();
    }

    /**
     * Create an instance of {@link PatientRecord }
     * 
     */
    public PatientRecord createPatientRecord() {
        return new PatientRecord();
    }

    /**
     * Create an instance of {@link Demographics.Contact }
     * 
     */
    public Demographics.Contact createDemographicsContact() {
        return new Demographics.Contact();
    }

    /**
     * Create an instance of {@link Demographics.PrimaryPhysician }
     * 
     */
    public Demographics.PrimaryPhysician createDemographicsPrimaryPhysician() {
        return new Demographics.PrimaryPhysician();
    }

    /**
     * Create an instance of {@link Demographics.PersonStatusCode }
     * 
     */
    public Demographics.PersonStatusCode createDemographicsPersonStatusCode() {
        return new Demographics.PersonStatusCode();
    }

    /**
     * Create an instance of {@link Demographics.PreferredPharmacy }
     * 
     */
    public Demographics.PreferredPharmacy createDemographicsPreferredPharmacy() {
        return new Demographics.PreferredPharmacy();
    }

    /**
     * Create an instance of {@link PersonalHistory }
     * 
     */
    public PersonalHistory createPersonalHistory() {
        return new PersonalHistory();
    }

    /**
     * Create an instance of {@link FamilyHistory }
     * 
     */
    public FamilyHistory createFamilyHistory() {
        return new FamilyHistory();
    }

    /**
     * Create an instance of {@link PastHealth }
     * 
     */
    public PastHealth createPastHealth() {
        return new PastHealth();
    }

    /**
     * Create an instance of {@link ProblemList }
     * 
     */
    public ProblemList createProblemList() {
        return new ProblemList();
    }

    /**
     * Create an instance of {@link RiskFactors }
     * 
     */
    public RiskFactors createRiskFactors() {
        return new RiskFactors();
    }

    /**
     * Create an instance of {@link AllergiesAndAdverseReactions }
     * 
     */
    public AllergiesAndAdverseReactions createAllergiesAndAdverseReactions() {
        return new AllergiesAndAdverseReactions();
    }

    /**
     * Create an instance of {@link MedicationsAndTreatments.PrescribedBy }
     * 
     */
    public MedicationsAndTreatments.PrescribedBy createMedicationsAndTreatmentsPrescribedBy() {
        return new MedicationsAndTreatments.PrescribedBy();
    }

    /**
     * Create an instance of {@link Immunizations }
     * 
     */
    public Immunizations createImmunizations() {
        return new Immunizations();
    }

    /**
     * Create an instance of {@link LaboratoryResults.Result }
     * 
     */
    public LaboratoryResults.Result createLaboratoryResultsResult() {
        return new LaboratoryResults.Result();
    }

    /**
     * Create an instance of {@link LaboratoryResults.ReferenceRange }
     * 
     */
    public LaboratoryResults.ReferenceRange createLaboratoryResultsReferenceRange() {
        return new LaboratoryResults.ReferenceRange();
    }

    /**
     * Create an instance of {@link LaboratoryResults.ResultReviewer }
     * 
     */
    public LaboratoryResults.ResultReviewer createLaboratoryResultsResultReviewer() {
        return new LaboratoryResults.ResultReviewer();
    }

    /**
     * Create an instance of {@link Appointments.Provider }
     * 
     */
    public Appointments.Provider createAppointmentsProvider() {
        return new Appointments.Provider();
    }

    /**
     * Create an instance of {@link ClinicalNotes.ParticipatingProviders }
     * 
     */
    public ClinicalNotes.ParticipatingProviders createClinicalNotesParticipatingProviders() {
        return new ClinicalNotes.ParticipatingProviders();
    }

    /**
     * Create an instance of {@link ClinicalNotes.NoteReviewer }
     * 
     */
    public ClinicalNotes.NoteReviewer createClinicalNotesNoteReviewer() {
        return new ClinicalNotes.NoteReviewer();
    }

    /**
     * Create an instance of {@link Reports.SourceAuthorPhysician }
     * 
     */
    public Reports.SourceAuthorPhysician createReportsSourceAuthorPhysician() {
        return new Reports.SourceAuthorPhysician();
    }

    /**
     * Create an instance of {@link Reports.ReportReviewed }
     * 
     */
    public Reports.ReportReviewed createReportsReportReviewed() {
        return new Reports.ReportReviewed();
    }

    /**
     * Create an instance of {@link Reports.OBRContent }
     * 
     */
    public Reports.OBRContent createReportsOBRContent() {
        return new Reports.OBRContent();
    }

    /**
     * Create an instance of {@link CareElements }
     * 
     */
    public CareElements createCareElements() {
        return new CareElements();
    }

    /**
     * Create an instance of {@link AlertsAndSpecialNeeds }
     * 
     */
    public AlertsAndSpecialNeeds createAlertsAndSpecialNeeds() {
        return new AlertsAndSpecialNeeds();
    }

    /**
     * Create an instance of {@link NewCategory }
     * 
     */
    public NewCategory createNewCategory() {
        return new NewCategory();
    }

    /**
     * Create an instance of {@link Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician }
     * 
     */
    public Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician createDemographicsEnrolmentEnrolmentHistoryEnrolledToPhysician() {
        return new Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician();
    }

    /**
     * Create an instance of {@link ResidualInformationForAlerts.DataElement }
     * 
     */
    public ResidualInformationForAlerts.DataElement createResidualInformationForAlertsDataElement() {
        return new ResidualInformationForAlerts.DataElement();
    }

    /**
     * Create an instance of {@link ResidualInformation.DataElement }
     * 
     */
    public ResidualInformation.DataElement createResidualInformationDataElement() {
        return new ResidualInformation.DataElement();
    }

    /**
     * Create an instance of {@link PersonNameStandard.OtherNames.OtherName }
     * 
     */
    public PersonNameStandard.OtherNames.OtherName createPersonNameStandardOtherNamesOtherName() {
        return new PersonNameStandard.OtherNames.OtherName();
    }

    /**
     * Create an instance of {@link PersonNameStandard.LegalName.FirstName }
     * 
     */
    public PersonNameStandard.LegalName.FirstName createPersonNameStandardLegalNameFirstName() {
        return new PersonNameStandard.LegalName.FirstName();
    }

    /**
     * Create an instance of {@link PersonNameStandard.LegalName.LastName }
     * 
     */
    public PersonNameStandard.LegalName.LastName createPersonNameStandardLegalNameLastName() {
        return new PersonNameStandard.LegalName.LastName();
    }

    /**
     * Create an instance of {@link PersonNameStandard.LegalName.OtherName }
     * 
     */
    public PersonNameStandard.LegalName.OtherName createPersonNameStandardLegalNameOtherName() {
        return new PersonNameStandard.LegalName.OtherName();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "cds_dt", name = "phoneNumber", scope = PhoneNumber.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPhoneNumberPhoneNumber(String value) {
        return new JAXBElement<String>(_PhoneNumberPhoneNumber_QNAME, String.class, PhoneNumber.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "cds_dt", name = "extension", scope = PhoneNumber.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPhoneNumberExtension(String value) {
        return new JAXBElement<String>(_PhoneNumberExtension_QNAME, String.class, PhoneNumber.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "cds_dt", name = "areaCode", scope = PhoneNumber.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPhoneNumberAreaCode(String value) {
        return new JAXBElement<String>(_PhoneNumberAreaCode_QNAME, String.class, PhoneNumber.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "cds_dt", name = "number", scope = PhoneNumber.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPhoneNumberNumber(String value) {
        return new JAXBElement<String>(_PhoneNumberNumber_QNAME, String.class, PhoneNumber.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "cds_dt", name = "exchange", scope = PhoneNumber.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPhoneNumberExchange(String value) {
        return new JAXBElement<String>(_PhoneNumberExchange_QNAME, String.class, PhoneNumber.class, value);
    }

}
