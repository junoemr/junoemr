
package xml.hrm.v4_3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personNameStandard complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="personNameStandard"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NamePrefix" type="{cds_dt}personNamePrefixCode" minOccurs="0"/&gt;
 *         &lt;element name="LegalName"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="FirstName"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;all&gt;
 *                             &lt;element name="Part"&gt;
 *                               &lt;simpleType&gt;
 *                                 &lt;restriction base="{cds_dt}personNamePart"&gt;
 *                                 &lt;/restriction&gt;
 *                               &lt;/simpleType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
 *                             &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
 *                           &lt;/all&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="LastName"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;all&gt;
 *                             &lt;element name="Part"&gt;
 *                               &lt;simpleType&gt;
 *                                 &lt;restriction base="{cds_dt}personNamePart"&gt;
 *                                 &lt;/restriction&gt;
 *                               &lt;/simpleType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
 *                             &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
 *                           &lt;/all&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="OtherName" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;all&gt;
 *                             &lt;element name="Part"&gt;
 *                               &lt;simpleType&gt;
 *                                 &lt;restriction base="{cds_dt}personNamePart"&gt;
 *                                 &lt;/restriction&gt;
 *                               &lt;/simpleType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
 *                             &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
 *                           &lt;/all&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="namePurpose" use="required" type="{cds_dt}personNamePurposeCode" fixed="L" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="OtherNames" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="OtherName" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;all&gt;
 *                             &lt;element name="Part"&gt;
 *                               &lt;simpleType&gt;
 *                                 &lt;restriction base="{cds_dt}personNamePart"&gt;
 *                                 &lt;/restriction&gt;
 *                               &lt;/simpleType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
 *                             &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
 *                           &lt;/all&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="namePurpose" use="required" type="{cds_dt}personNamePurposeCode" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LastNameSuffix" type="{cds_dt}personNameSuffixCode" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personNameStandard", propOrder = {
    "namePrefix",
    "legalName",
    "otherNames",
    "lastNameSuffix"
})
public class PersonNameStandard {

    @XmlElement(name = "NamePrefix")
    @XmlSchemaType(name = "token")
    protected PersonNamePrefixCode namePrefix;
    @XmlElement(name = "LegalName", required = true)
    protected PersonNameStandard.LegalName legalName;
    @XmlElement(name = "OtherNames")
    protected List<PersonNameStandard.OtherNames> otherNames;
    @XmlElement(name = "LastNameSuffix")
    @XmlSchemaType(name = "string")
    protected PersonNameSuffixCode lastNameSuffix;

    /**
     * Gets the value of the namePrefix property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNamePrefixCode }
     *     
     */
    public PersonNamePrefixCode getNamePrefix() {
        return namePrefix;
    }

    /**
     * Sets the value of the namePrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNamePrefixCode }
     *     
     */
    public void setNamePrefix(PersonNamePrefixCode value) {
        this.namePrefix = value;
    }

    /**
     * Gets the value of the legalName property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameStandard.LegalName }
     *     
     */
    public PersonNameStandard.LegalName getLegalName() {
        return legalName;
    }

    /**
     * Sets the value of the legalName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameStandard.LegalName }
     *     
     */
    public void setLegalName(PersonNameStandard.LegalName value) {
        this.legalName = value;
    }

    /**
     * Gets the value of the otherNames property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherNames property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherNames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonNameStandard.OtherNames }
     * 
     * 
     */
    public List<PersonNameStandard.OtherNames> getOtherNames() {
        if (otherNames == null) {
            otherNames = new ArrayList<PersonNameStandard.OtherNames>();
        }
        return this.otherNames;
    }

    /**
     * Gets the value of the lastNameSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameSuffixCode }
     *     
     */
    public PersonNameSuffixCode getLastNameSuffix() {
        return lastNameSuffix;
    }

    /**
     * Sets the value of the lastNameSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameSuffixCode }
     *     
     */
    public void setLastNameSuffix(PersonNameSuffixCode value) {
        this.lastNameSuffix = value;
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
     *       &lt;sequence&gt;
     *         &lt;element name="FirstName"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;all&gt;
     *                   &lt;element name="Part"&gt;
     *                     &lt;simpleType&gt;
     *                       &lt;restriction base="{cds_dt}personNamePart"&gt;
     *                       &lt;/restriction&gt;
     *                     &lt;/simpleType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
     *                   &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
     *                 &lt;/all&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="LastName"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;all&gt;
     *                   &lt;element name="Part"&gt;
     *                     &lt;simpleType&gt;
     *                       &lt;restriction base="{cds_dt}personNamePart"&gt;
     *                       &lt;/restriction&gt;
     *                     &lt;/simpleType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
     *                   &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
     *                 &lt;/all&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="OtherName" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;all&gt;
     *                   &lt;element name="Part"&gt;
     *                     &lt;simpleType&gt;
     *                       &lt;restriction base="{cds_dt}personNamePart"&gt;
     *                       &lt;/restriction&gt;
     *                     &lt;/simpleType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
     *                   &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
     *                 &lt;/all&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="namePurpose" use="required" type="{cds_dt}personNamePurposeCode" fixed="L" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "firstName",
        "lastName",
        "otherName"
    })
    public static class LegalName {

        @XmlElement(name = "FirstName", required = true)
        protected PersonNameStandard.LegalName.FirstName firstName;
        @XmlElement(name = "LastName", required = true)
        protected PersonNameStandard.LegalName.LastName lastName;
        @XmlElement(name = "OtherName")
        protected List<PersonNameStandard.LegalName.OtherName> otherName;
        @XmlAttribute(name = "namePurpose", required = true)
        protected PersonNamePurposeCode namePurpose;

        /**
         * Gets the value of the firstName property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNameStandard.LegalName.FirstName }
         *     
         */
        public PersonNameStandard.LegalName.FirstName getFirstName() {
            return firstName;
        }

        /**
         * Sets the value of the firstName property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNameStandard.LegalName.FirstName }
         *     
         */
        public void setFirstName(PersonNameStandard.LegalName.FirstName value) {
            this.firstName = value;
        }

        /**
         * Gets the value of the lastName property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNameStandard.LegalName.LastName }
         *     
         */
        public PersonNameStandard.LegalName.LastName getLastName() {
            return lastName;
        }

        /**
         * Sets the value of the lastName property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNameStandard.LegalName.LastName }
         *     
         */
        public void setLastName(PersonNameStandard.LegalName.LastName value) {
            this.lastName = value;
        }

        /**
         * Gets the value of the otherName property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the otherName property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOtherName().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PersonNameStandard.LegalName.OtherName }
         * 
         * 
         */
        public List<PersonNameStandard.LegalName.OtherName> getOtherName() {
            if (otherName == null) {
                otherName = new ArrayList<PersonNameStandard.LegalName.OtherName>();
            }
            return this.otherName;
        }

        /**
         * Gets the value of the namePurpose property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNamePurposeCode }
         *     
         */
        public PersonNamePurposeCode getNamePurpose() {
            if (namePurpose == null) {
                return PersonNamePurposeCode.L;
            } else {
                return namePurpose;
            }
        }

        /**
         * Sets the value of the namePurpose property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNamePurposeCode }
         *     
         */
        public void setNamePurpose(PersonNamePurposeCode value) {
            this.namePurpose = value;
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
         *         &lt;element name="Part"&gt;
         *           &lt;simpleType&gt;
         *             &lt;restriction base="{cds_dt}personNamePart"&gt;
         *             &lt;/restriction&gt;
         *           &lt;/simpleType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
         *         &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
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
        public static class FirstName {

            @XmlElement(name = "Part", required = true)
            protected String part;
            @XmlElement(name = "PartType", required = true)
            @XmlSchemaType(name = "token")
            protected PersonNamePartTypeCode partType;
            @XmlElement(name = "PartQualifier")
            @XmlSchemaType(name = "token")
            protected PersonNamePartQualifierCode partQualifier;

            /**
             * Gets the value of the part property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPart() {
                return part;
            }

            /**
             * Sets the value of the part property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPart(String value) {
                this.part = value;
            }

            /**
             * Gets the value of the partType property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public PersonNamePartTypeCode getPartType() {
                return partType;
            }

            /**
             * Sets the value of the partType property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public void setPartType(PersonNamePartTypeCode value) {
                this.partType = value;
            }

            /**
             * Gets the value of the partQualifier property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public PersonNamePartQualifierCode getPartQualifier() {
                return partQualifier;
            }

            /**
             * Sets the value of the partQualifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public void setPartQualifier(PersonNamePartQualifierCode value) {
                this.partQualifier = value;
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
         *         &lt;element name="Part"&gt;
         *           &lt;simpleType&gt;
         *             &lt;restriction base="{cds_dt}personNamePart"&gt;
         *             &lt;/restriction&gt;
         *           &lt;/simpleType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
         *         &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
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
        public static class LastName {

            @XmlElement(name = "Part", required = true)
            protected String part;
            @XmlElement(name = "PartType", required = true)
            @XmlSchemaType(name = "token")
            protected PersonNamePartTypeCode partType;
            @XmlElement(name = "PartQualifier")
            @XmlSchemaType(name = "token")
            protected PersonNamePartQualifierCode partQualifier;

            /**
             * Gets the value of the part property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPart() {
                return part;
            }

            /**
             * Sets the value of the part property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPart(String value) {
                this.part = value;
            }

            /**
             * Gets the value of the partType property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public PersonNamePartTypeCode getPartType() {
                return partType;
            }

            /**
             * Sets the value of the partType property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public void setPartType(PersonNamePartTypeCode value) {
                this.partType = value;
            }

            /**
             * Gets the value of the partQualifier property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public PersonNamePartQualifierCode getPartQualifier() {
                return partQualifier;
            }

            /**
             * Sets the value of the partQualifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public void setPartQualifier(PersonNamePartQualifierCode value) {
                this.partQualifier = value;
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
         *         &lt;element name="Part"&gt;
         *           &lt;simpleType&gt;
         *             &lt;restriction base="{cds_dt}personNamePart"&gt;
         *             &lt;/restriction&gt;
         *           &lt;/simpleType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
         *         &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
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
        public static class OtherName {

            @XmlElement(name = "Part", required = true)
            protected String part;
            @XmlElement(name = "PartType", required = true)
            @XmlSchemaType(name = "token")
            protected PersonNamePartTypeCode partType;
            @XmlElement(name = "PartQualifier")
            @XmlSchemaType(name = "token")
            protected PersonNamePartQualifierCode partQualifier;

            /**
             * Gets the value of the part property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPart() {
                return part;
            }

            /**
             * Sets the value of the part property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPart(String value) {
                this.part = value;
            }

            /**
             * Gets the value of the partType property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public PersonNamePartTypeCode getPartType() {
                return partType;
            }

            /**
             * Sets the value of the partType property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public void setPartType(PersonNamePartTypeCode value) {
                this.partType = value;
            }

            /**
             * Gets the value of the partQualifier property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public PersonNamePartQualifierCode getPartQualifier() {
                return partQualifier;
            }

            /**
             * Sets the value of the partQualifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public void setPartQualifier(PersonNamePartQualifierCode value) {
                this.partQualifier = value;
            }

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
     *       &lt;sequence&gt;
     *         &lt;element name="OtherName" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;all&gt;
     *                   &lt;element name="Part"&gt;
     *                     &lt;simpleType&gt;
     *                       &lt;restriction base="{cds_dt}personNamePart"&gt;
     *                       &lt;/restriction&gt;
     *                     &lt;/simpleType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
     *                   &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
     *                 &lt;/all&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="namePurpose" use="required" type="{cds_dt}personNamePurposeCode" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "otherName"
    })
    public static class OtherNames {

        @XmlElement(name = "OtherName")
        protected List<PersonNameStandard.OtherNames.OtherName> otherName;
        @XmlAttribute(name = "namePurpose", required = true)
        protected PersonNamePurposeCode namePurpose;

        /**
         * Gets the value of the otherName property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the otherName property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOtherName().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PersonNameStandard.OtherNames.OtherName }
         * 
         * 
         */
        public List<PersonNameStandard.OtherNames.OtherName> getOtherName() {
            if (otherName == null) {
                otherName = new ArrayList<PersonNameStandard.OtherNames.OtherName>();
            }
            return this.otherName;
        }

        /**
         * Gets the value of the namePurpose property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNamePurposeCode }
         *     
         */
        public PersonNamePurposeCode getNamePurpose() {
            return namePurpose;
        }

        /**
         * Sets the value of the namePurpose property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNamePurposeCode }
         *     
         */
        public void setNamePurpose(PersonNamePurposeCode value) {
            this.namePurpose = value;
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
         *         &lt;element name="Part"&gt;
         *           &lt;simpleType&gt;
         *             &lt;restriction base="{cds_dt}personNamePart"&gt;
         *             &lt;/restriction&gt;
         *           &lt;/simpleType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="PartType" type="{cds_dt}personNamePartTypeCode"/&gt;
         *         &lt;element name="PartQualifier" type="{cds_dt}personNamePartQualifierCode" minOccurs="0"/&gt;
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
        public static class OtherName {

            @XmlElement(name = "Part", required = true)
            protected String part;
            @XmlElement(name = "PartType", required = true)
            @XmlSchemaType(name = "token")
            protected PersonNamePartTypeCode partType;
            @XmlElement(name = "PartQualifier")
            @XmlSchemaType(name = "token")
            protected PersonNamePartQualifierCode partQualifier;

            /**
             * Gets the value of the part property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPart() {
                return part;
            }

            /**
             * Sets the value of the part property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPart(String value) {
                this.part = value;
            }

            /**
             * Gets the value of the partType property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public PersonNamePartTypeCode getPartType() {
                return partType;
            }

            /**
             * Sets the value of the partType property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartTypeCode }
             *     
             */
            public void setPartType(PersonNamePartTypeCode value) {
                this.partType = value;
            }

            /**
             * Gets the value of the partQualifier property.
             * 
             * @return
             *     possible object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public PersonNamePartQualifierCode getPartQualifier() {
                return partQualifier;
            }

            /**
             * Sets the value of the partQualifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonNamePartQualifierCode }
             *     
             */
            public void setPartQualifier(PersonNamePartQualifierCode value) {
                this.partQualifier = value;
            }

        }

    }

}
