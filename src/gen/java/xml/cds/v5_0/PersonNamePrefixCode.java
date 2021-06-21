
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personNamePrefixCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="personNamePrefixCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="6"/&gt;
 *     &lt;enumeration value="Bro"/&gt;
 *     &lt;enumeration value="Capt"/&gt;
 *     &lt;enumeration value="Chief"/&gt;
 *     &lt;enumeration value="Cst"/&gt;
 *     &lt;enumeration value="Corp"/&gt;
 *     &lt;enumeration value="Dr"/&gt;
 *     &lt;enumeration value="Fr"/&gt;
 *     &lt;enumeration value="Hon"/&gt;
 *     &lt;enumeration value="Lt"/&gt;
 *     &lt;enumeration value="Madam"/&gt;
 *     &lt;enumeration value="Mme"/&gt;
 *     &lt;enumeration value="Mlle"/&gt;
 *     &lt;enumeration value="Major"/&gt;
 *     &lt;enumeration value="Mayor"/&gt;
 *     &lt;enumeration value="Miss"/&gt;
 *     &lt;enumeration value="Mr"/&gt;
 *     &lt;enumeration value="Mssr"/&gt;
 *     &lt;enumeration value="Mrs"/&gt;
 *     &lt;enumeration value="Ms"/&gt;
 *     &lt;enumeration value="Prof"/&gt;
 *     &lt;enumeration value="Reeve"/&gt;
 *     &lt;enumeration value="Rev"/&gt;
 *     &lt;enumeration value="RtHon"/&gt;
 *     &lt;enumeration value="Sen"/&gt;
 *     &lt;enumeration value="Sgt"/&gt;
 *     &lt;enumeration value="Sr"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "personNamePrefixCode")
@XmlEnum
public enum PersonNamePrefixCode {

    @XmlEnumValue("Bro")
    BRO("Bro"),
    @XmlEnumValue("Capt")
    CAPT("Capt"),
    @XmlEnumValue("Chief")
    CHIEF("Chief"),
    @XmlEnumValue("Cst")
    CST("Cst"),
    @XmlEnumValue("Corp")
    CORP("Corp"),
    @XmlEnumValue("Dr")
    DR("Dr"),
    @XmlEnumValue("Fr")
    FR("Fr"),
    @XmlEnumValue("Hon")
    HON("Hon"),
    @XmlEnumValue("Lt")
    LT("Lt"),
    @XmlEnumValue("Madam")
    MADAM("Madam"),
    @XmlEnumValue("Mme")
    MME("Mme"),
    @XmlEnumValue("Mlle")
    MLLE("Mlle"),
    @XmlEnumValue("Major")
    MAJOR("Major"),
    @XmlEnumValue("Mayor")
    MAYOR("Mayor"),
    @XmlEnumValue("Miss")
    MISS("Miss"),
    @XmlEnumValue("Mr")
    MR("Mr"),
    @XmlEnumValue("Mssr")
    MSSR("Mssr"),
    @XmlEnumValue("Mrs")
    MRS("Mrs"),
    @XmlEnumValue("Ms")
    MS("Ms"),
    @XmlEnumValue("Prof")
    PROF("Prof"),
    @XmlEnumValue("Reeve")
    REEVE("Reeve"),
    @XmlEnumValue("Rev")
    REV("Rev"),
    @XmlEnumValue("RtHon")
    RT_HON("RtHon"),
    @XmlEnumValue("Sen")
    SEN("Sen"),
    @XmlEnumValue("Sgt")
    SGT("Sgt"),
    @XmlEnumValue("Sr")
    SR("Sr");
    private final String value;

    PersonNamePrefixCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PersonNamePrefixCode fromValue(String v) {
        for (PersonNamePrefixCode c: PersonNamePrefixCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
