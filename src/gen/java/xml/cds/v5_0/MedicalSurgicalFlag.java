
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for medicalSurgicalFlag.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="medicalSurgicalFlag"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="1"/&gt;
 *     &lt;enumeration value="M"/&gt;
 *     &lt;enumeration value="S"/&gt;
 *     &lt;enumeration value="U"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "medicalSurgicalFlag")
@XmlEnum
public enum MedicalSurgicalFlag {

    M,
    S,
    U;

    public String value() {
        return name();
    }

    public static MedicalSurgicalFlag fromValue(String v) {
        return valueOf(v);
    }

}
