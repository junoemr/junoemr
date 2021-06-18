
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for officialSpokenLanguageCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="officialSpokenLanguageCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="3"/&gt;
 *     &lt;enumeration value="ENG"/&gt;
 *     &lt;enumeration value="FRE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "officialSpokenLanguageCode")
@XmlEnum
public enum OfficialSpokenLanguageCode {

    ENG,
    FRE;

    public String value() {
        return name();
    }

    public static OfficialSpokenLanguageCode fromValue(String v) {
        return valueOf(v);
    }

}
