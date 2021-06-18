
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="ResidualInfo" type="{cds_dt}residualInformation" minOccurs="0"/&gt;
 *         &lt;element name="OffendingAgentDescription" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PropertyOfOffendingAgent" type="{cds_dt}propertyOfOffendingAgent" minOccurs="0"/&gt;
 *         &lt;element name="Code" type="{cds_dt}DrugCode" minOccurs="0"/&gt;
 *         &lt;element name="ReactionType" type="{cds_dt}adverseReactionType" minOccurs="0"/&gt;
 *         &lt;element name="StartDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="LifeStage" type="{cds_dt}lifeStage" minOccurs="0"/&gt;
 *         &lt;element name="Severity" type="{cds_dt}adverseReactionSeverity" minOccurs="0"/&gt;
 *         &lt;element name="Reaction" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RecordedDate" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{cds_dt}text32K" minOccurs="0"/&gt;
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
    "residualInfo",
    "offendingAgentDescription",
    "propertyOfOffendingAgent",
    "code",
    "reactionType",
    "startDate",
    "lifeStage",
    "severity",
    "reaction",
    "recordedDate",
    "notes"
})
@XmlRootElement(name = "AllergiesAndAdverseReactions", namespace = "cds")
public class AllergiesAndAdverseReactions {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "OffendingAgentDescription", namespace = "cds")
    protected String offendingAgentDescription;
    @XmlElement(name = "PropertyOfOffendingAgent", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected PropertyOfOffendingAgent propertyOfOffendingAgent;
    @XmlElement(name = "Code", namespace = "cds")
    protected DrugCode code;
    @XmlElement(name = "ReactionType", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected AdverseReactionType reactionType;
    @XmlElement(name = "StartDate", namespace = "cds")
    protected DateFullOrPartial startDate;
    @XmlElement(name = "LifeStage", namespace = "cds")
    @XmlSchemaType(name = "string")
    protected LifeStage lifeStage;
    @XmlElement(name = "Severity", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected AdverseReactionSeverity severity;
    @XmlElement(name = "Reaction", namespace = "cds")
    protected String reaction;
    @XmlElement(name = "RecordedDate", namespace = "cds")
    protected DateTimeFullOrPartial recordedDate;
    @XmlElement(name = "Notes", namespace = "cds")
    protected String notes;

    /**
     * Gets the value of the residualInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ResidualInformation }
     *     
     */
    public ResidualInformation getResidualInfo() {
        return residualInfo;
    }

    /**
     * Sets the value of the residualInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResidualInformation }
     *     
     */
    public void setResidualInfo(ResidualInformation value) {
        this.residualInfo = value;
    }

    /**
     * Gets the value of the offendingAgentDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOffendingAgentDescription() {
        return offendingAgentDescription;
    }

    /**
     * Sets the value of the offendingAgentDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOffendingAgentDescription(String value) {
        this.offendingAgentDescription = value;
    }

    /**
     * Gets the value of the propertyOfOffendingAgent property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyOfOffendingAgent }
     *     
     */
    public PropertyOfOffendingAgent getPropertyOfOffendingAgent() {
        return propertyOfOffendingAgent;
    }

    /**
     * Sets the value of the propertyOfOffendingAgent property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyOfOffendingAgent }
     *     
     */
    public void setPropertyOfOffendingAgent(PropertyOfOffendingAgent value) {
        this.propertyOfOffendingAgent = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link DrugCode }
     *     
     */
    public DrugCode getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link DrugCode }
     *     
     */
    public void setCode(DrugCode value) {
        this.code = value;
    }

    /**
     * Gets the value of the reactionType property.
     * 
     * @return
     *     possible object is
     *     {@link AdverseReactionType }
     *     
     */
    public AdverseReactionType getReactionType() {
        return reactionType;
    }

    /**
     * Sets the value of the reactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdverseReactionType }
     *     
     */
    public void setReactionType(AdverseReactionType value) {
        this.reactionType = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setStartDate(DateFullOrPartial value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the lifeStage property.
     * 
     * @return
     *     possible object is
     *     {@link LifeStage }
     *     
     */
    public LifeStage getLifeStage() {
        return lifeStage;
    }

    /**
     * Sets the value of the lifeStage property.
     * 
     * @param value
     *     allowed object is
     *     {@link LifeStage }
     *     
     */
    public void setLifeStage(LifeStage value) {
        this.lifeStage = value;
    }

    /**
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link AdverseReactionSeverity }
     *     
     */
    public AdverseReactionSeverity getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdverseReactionSeverity }
     *     
     */
    public void setSeverity(AdverseReactionSeverity value) {
        this.severity = value;
    }

    /**
     * Gets the value of the reaction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReaction() {
        return reaction;
    }

    /**
     * Sets the value of the reaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReaction(String value) {
        this.reaction = value;
    }

    /**
     * Gets the value of the recordedDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getRecordedDate() {
        return recordedDate;
    }

    /**
     * Sets the value of the recordedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setRecordedDate(DateTimeFullOrPartial value) {
        this.recordedDate = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

}
