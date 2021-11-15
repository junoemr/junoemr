
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personNamePartQualifierCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="personNamePartQualifierCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="2"/&gt;
 *     &lt;enumeration value="BR"/&gt;
 *     &lt;enumeration value="SP"/&gt;
 *     &lt;enumeration value="CL"/&gt;
 *     &lt;enumeration value="IN"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "personNamePartQualifierCode")
@XmlEnum
public enum PersonNamePartQualifierCode {

    BR,
    SP,
    CL,
    IN;

    public String value() {
        return name();
    }

    public static PersonNamePartQualifierCode fromValue(String v) {
        return valueOf(v);
    }

}
