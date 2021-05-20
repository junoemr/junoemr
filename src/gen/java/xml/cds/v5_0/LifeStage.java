
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for lifeStage.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="lifeStage"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;maxLength value="1"/&gt;
 *     &lt;enumeration value="N"/&gt;
 *     &lt;enumeration value="I"/&gt;
 *     &lt;enumeration value="C"/&gt;
 *     &lt;enumeration value="T"/&gt;
 *     &lt;enumeration value="A"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "lifeStage")
@XmlEnum
public enum LifeStage {

    N,
    I,
    C,
    T,
    A;

    public String value() {
        return name();
    }

    public static LifeStage fromValue(String v) {
        return valueOf(v);
    }

}
