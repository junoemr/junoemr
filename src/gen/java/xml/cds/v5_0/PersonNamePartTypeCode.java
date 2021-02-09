
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personNamePartTypeCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="personNamePartTypeCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="4"/&gt;
 *     &lt;enumeration value="FAMC"/&gt;
 *     &lt;enumeration value="GIV"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "personNamePartTypeCode")
@XmlEnum
public enum PersonNamePartTypeCode {

    FAMC,
    GIV;

    public String value() {
        return name();
    }

    public static PersonNamePartTypeCode fromValue(String v) {
        return valueOf(v);
    }

}
