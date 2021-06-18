
package xml.cds.v5_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="SmokingStatus" type="{cds_dt}smokingStatus" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SmokingPacks" type="{cds_dt}smokingPacks" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Weight" type="{cds_dt}weight" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Height" type="{cds_dt}height" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="WaistCircumference" type="{cds_dt}waistCircumference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="BloodPressure" type="{cds_dt}bloodPressure" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DiabetesComplicationsScreening" type="{cds_dt}diabetesComplicationScreening" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DiabetesMotivationalCounselling" type="{cds_dt}diabetesMotivationalCounselling" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DiabetesSelfManagementCollaborative" type="{cds_dt}diabetesSelfManagementCollaborative" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DiabetesSelfManagementChallenges" type="{cds_dt}diabetesSelfManagementChallenges" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DiabetesEducationalSelfManagement" type="{cds_dt}diabetesEducationalSelfManagement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="HypoglycemicEpisodes" type="{cds_dt}hypoglycemicEpisodes" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SelfMonitoringBloodGlucose" type="{cds_dt}selfMonitoringBloodGlucose" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "smokingStatus",
    "smokingPacks",
    "weight",
    "height",
    "waistCircumference",
    "bloodPressure",
    "diabetesComplicationsScreening",
    "diabetesMotivationalCounselling",
    "diabetesSelfManagementCollaborative",
    "diabetesSelfManagementChallenges",
    "diabetesEducationalSelfManagement",
    "hypoglycemicEpisodes",
    "selfMonitoringBloodGlucose"
})
@XmlRootElement(name = "CareElements", namespace = "cds")
public class CareElements {

    @XmlElement(name = "SmokingStatus", namespace = "cds")
    protected List<SmokingStatus> smokingStatus;
    @XmlElement(name = "SmokingPacks", namespace = "cds")
    protected List<SmokingPacks> smokingPacks;
    @XmlElement(name = "Weight", namespace = "cds")
    protected List<Weight> weight;
    @XmlElement(name = "Height", namespace = "cds")
    protected List<Height> height;
    @XmlElement(name = "WaistCircumference", namespace = "cds")
    protected List<WaistCircumference> waistCircumference;
    @XmlElement(name = "BloodPressure", namespace = "cds")
    protected List<BloodPressure> bloodPressure;
    @XmlElement(name = "DiabetesComplicationsScreening", namespace = "cds")
    protected List<DiabetesComplicationScreening> diabetesComplicationsScreening;
    @XmlElement(name = "DiabetesMotivationalCounselling", namespace = "cds")
    protected List<DiabetesMotivationalCounselling> diabetesMotivationalCounselling;
    @XmlElement(name = "DiabetesSelfManagementCollaborative", namespace = "cds")
    protected List<DiabetesSelfManagementCollaborative> diabetesSelfManagementCollaborative;
    @XmlElement(name = "DiabetesSelfManagementChallenges", namespace = "cds")
    protected List<DiabetesSelfManagementChallenges> diabetesSelfManagementChallenges;
    @XmlElement(name = "DiabetesEducationalSelfManagement", namespace = "cds")
    protected List<DiabetesEducationalSelfManagement> diabetesEducationalSelfManagement;
    @XmlElement(name = "HypoglycemicEpisodes", namespace = "cds")
    protected List<HypoglycemicEpisodes> hypoglycemicEpisodes;
    @XmlElement(name = "SelfMonitoringBloodGlucose", namespace = "cds")
    protected List<SelfMonitoringBloodGlucose> selfMonitoringBloodGlucose;

    /**
     * Gets the value of the smokingStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smokingStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmokingStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SmokingStatus }
     * 
     * 
     */
    public List<SmokingStatus> getSmokingStatus() {
        if (smokingStatus == null) {
            smokingStatus = new ArrayList<SmokingStatus>();
        }
        return this.smokingStatus;
    }

    /**
     * Gets the value of the smokingPacks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smokingPacks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmokingPacks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SmokingPacks }
     * 
     * 
     */
    public List<SmokingPacks> getSmokingPacks() {
        if (smokingPacks == null) {
            smokingPacks = new ArrayList<SmokingPacks>();
        }
        return this.smokingPacks;
    }

    /**
     * Gets the value of the weight property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the weight property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWeight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Weight }
     * 
     * 
     */
    public List<Weight> getWeight() {
        if (weight == null) {
            weight = new ArrayList<Weight>();
        }
        return this.weight;
    }

    /**
     * Gets the value of the height property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the height property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Height }
     * 
     * 
     */
    public List<Height> getHeight() {
        if (height == null) {
            height = new ArrayList<Height>();
        }
        return this.height;
    }

    /**
     * Gets the value of the waistCircumference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the waistCircumference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWaistCircumference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WaistCircumference }
     * 
     * 
     */
    public List<WaistCircumference> getWaistCircumference() {
        if (waistCircumference == null) {
            waistCircumference = new ArrayList<WaistCircumference>();
        }
        return this.waistCircumference;
    }

    /**
     * Gets the value of the bloodPressure property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bloodPressure property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBloodPressure().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BloodPressure }
     * 
     * 
     */
    public List<BloodPressure> getBloodPressure() {
        if (bloodPressure == null) {
            bloodPressure = new ArrayList<BloodPressure>();
        }
        return this.bloodPressure;
    }

    /**
     * Gets the value of the diabetesComplicationsScreening property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diabetesComplicationsScreening property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiabetesComplicationsScreening().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DiabetesComplicationScreening }
     * 
     * 
     */
    public List<DiabetesComplicationScreening> getDiabetesComplicationsScreening() {
        if (diabetesComplicationsScreening == null) {
            diabetesComplicationsScreening = new ArrayList<DiabetesComplicationScreening>();
        }
        return this.diabetesComplicationsScreening;
    }

    /**
     * Gets the value of the diabetesMotivationalCounselling property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diabetesMotivationalCounselling property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiabetesMotivationalCounselling().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DiabetesMotivationalCounselling }
     * 
     * 
     */
    public List<DiabetesMotivationalCounselling> getDiabetesMotivationalCounselling() {
        if (diabetesMotivationalCounselling == null) {
            diabetesMotivationalCounselling = new ArrayList<DiabetesMotivationalCounselling>();
        }
        return this.diabetesMotivationalCounselling;
    }

    /**
     * Gets the value of the diabetesSelfManagementCollaborative property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diabetesSelfManagementCollaborative property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiabetesSelfManagementCollaborative().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DiabetesSelfManagementCollaborative }
     * 
     * 
     */
    public List<DiabetesSelfManagementCollaborative> getDiabetesSelfManagementCollaborative() {
        if (diabetesSelfManagementCollaborative == null) {
            diabetesSelfManagementCollaborative = new ArrayList<DiabetesSelfManagementCollaborative>();
        }
        return this.diabetesSelfManagementCollaborative;
    }

    /**
     * Gets the value of the diabetesSelfManagementChallenges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diabetesSelfManagementChallenges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiabetesSelfManagementChallenges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DiabetesSelfManagementChallenges }
     * 
     * 
     */
    public List<DiabetesSelfManagementChallenges> getDiabetesSelfManagementChallenges() {
        if (diabetesSelfManagementChallenges == null) {
            diabetesSelfManagementChallenges = new ArrayList<DiabetesSelfManagementChallenges>();
        }
        return this.diabetesSelfManagementChallenges;
    }

    /**
     * Gets the value of the diabetesEducationalSelfManagement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diabetesEducationalSelfManagement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiabetesEducationalSelfManagement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DiabetesEducationalSelfManagement }
     * 
     * 
     */
    public List<DiabetesEducationalSelfManagement> getDiabetesEducationalSelfManagement() {
        if (diabetesEducationalSelfManagement == null) {
            diabetesEducationalSelfManagement = new ArrayList<DiabetesEducationalSelfManagement>();
        }
        return this.diabetesEducationalSelfManagement;
    }

    /**
     * Gets the value of the hypoglycemicEpisodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hypoglycemicEpisodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHypoglycemicEpisodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HypoglycemicEpisodes }
     * 
     * 
     */
    public List<HypoglycemicEpisodes> getHypoglycemicEpisodes() {
        if (hypoglycemicEpisodes == null) {
            hypoglycemicEpisodes = new ArrayList<HypoglycemicEpisodes>();
        }
        return this.hypoglycemicEpisodes;
    }

    /**
     * Gets the value of the selfMonitoringBloodGlucose property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the selfMonitoringBloodGlucose property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSelfMonitoringBloodGlucose().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SelfMonitoringBloodGlucose }
     * 
     * 
     */
    public List<SelfMonitoringBloodGlucose> getSelfMonitoringBloodGlucose() {
        if (selfMonitoringBloodGlucose == null) {
            selfMonitoringBloodGlucose = new ArrayList<SelfMonitoringBloodGlucose>();
        }
        return this.selfMonitoringBloodGlucose;
    }

}
