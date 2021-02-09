
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for immunizationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="immunizationType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="20"/&gt;
 *     &lt;enumeration value="BCG"/&gt;
 *     &lt;enumeration value="Chol-O"/&gt;
 *     &lt;enumeration value="Chol-Ecol-O"/&gt;
 *     &lt;enumeration value="CholEcol"/&gt;
 *     &lt;enumeration value="DTaP"/&gt;
 *     &lt;enumeration value="DTaP-IPV"/&gt;
 *     &lt;enumeration value="DTaP-IPV-Hib"/&gt;
 *     &lt;enumeration value="DTaP-IPV-Hib-HB"/&gt;
 *     &lt;enumeration value="DTaP-IPV-HB"/&gt;
 *     &lt;enumeration value="DTaP-Hib"/&gt;
 *     &lt;enumeration value="DT-IPV"/&gt;
 *     &lt;enumeration value="HA"/&gt;
 *     &lt;enumeration value="HAHB"/&gt;
 *     &lt;enumeration value="HA-Typh-I"/&gt;
 *     &lt;enumeration value="HB"/&gt;
 *     &lt;enumeration value="HBTmf"/&gt;
 *     &lt;enumeration value="Hib"/&gt;
 *     &lt;enumeration value="Inf"/&gt;
 *     &lt;enumeration value="IPV"/&gt;
 *     &lt;enumeration value="JE"/&gt;
 *     &lt;enumeration value="Men"/&gt;
 *     &lt;enumeration value="Men B"/&gt;
 *     &lt;enumeration value="Men-C"/&gt;
 *     &lt;enumeration value="Men-P-AC"/&gt;
 *     &lt;enumeration value="Men-P-ACWY"/&gt;
 *     &lt;enumeration value="MMR"/&gt;
 *     &lt;enumeration value="MR"/&gt;
 *     &lt;enumeration value="Pneu"/&gt;
 *     &lt;enumeration value="Pneu-C-7"/&gt;
 *     &lt;enumeration value="Pneu-P-23"/&gt;
 *     &lt;enumeration value="Rab"/&gt;
 *     &lt;enumeration value="T"/&gt;
 *     &lt;enumeration value="Td"/&gt;
 *     &lt;enumeration value="Tdap"/&gt;
 *     &lt;enumeration value="TdapIPV"/&gt;
 *     &lt;enumeration value="Td-IPV"/&gt;
 *     &lt;enumeration value="TBE"/&gt;
 *     &lt;enumeration value="Typh"/&gt;
 *     &lt;enumeration value="Typh (HA)"/&gt;
 *     &lt;enumeration value="Typh-I"/&gt;
 *     &lt;enumeration value="Typh-O"/&gt;
 *     &lt;enumeration value="Var"/&gt;
 *     &lt;enumeration value="YF"/&gt;
 *     &lt;enumeration value="HPV"/&gt;
 *     &lt;enumeration value="MMR-Var"/&gt;
 *     &lt;enumeration value="ROT"/&gt;
 *     &lt;enumeration value="Zos"/&gt;
 *     &lt;enumeration value="BAtx"/&gt;
 *     &lt;enumeration value="CMVIg"/&gt;
 *     &lt;enumeration value="DAtx"/&gt;
 *     &lt;enumeration value="HBIg"/&gt;
 *     &lt;enumeration value="Ig"/&gt;
 *     &lt;enumeration value="RabIg"/&gt;
 *     &lt;enumeration value="RSVAb"/&gt;
 *     &lt;enumeration value="RSVIg"/&gt;
 *     &lt;enumeration value="TIg"/&gt;
 *     &lt;enumeration value="VarIg"/&gt;
 *     &lt;enumeration value="VIG"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "immunizationType")
@XmlEnum
public enum ImmunizationType {

    BCG("BCG"),
    @XmlEnumValue("Chol-O")
    CHOL_O("Chol-O"),
    @XmlEnumValue("Chol-Ecol-O")
    CHOL_ECOL_O("Chol-Ecol-O"),
    @XmlEnumValue("CholEcol")
    CHOL_ECOL("CholEcol"),
    @XmlEnumValue("DTaP")
    D_TA_P("DTaP"),
    @XmlEnumValue("DTaP-IPV")
    D_TA_P_IPV("DTaP-IPV"),
    @XmlEnumValue("DTaP-IPV-Hib")
    D_TA_P_IPV_HIB("DTaP-IPV-Hib"),
    @XmlEnumValue("DTaP-IPV-Hib-HB")
    D_TA_P_IPV_HIB_HB("DTaP-IPV-Hib-HB"),
    @XmlEnumValue("DTaP-IPV-HB")
    D_TA_P_IPV_HB("DTaP-IPV-HB"),
    @XmlEnumValue("DTaP-Hib")
    D_TA_P_HIB("DTaP-Hib"),
    @XmlEnumValue("DT-IPV")
    DT_IPV("DT-IPV"),
    HA("HA"),
    HAHB("HAHB"),
    @XmlEnumValue("HA-Typh-I")
    HA_TYPH_I("HA-Typh-I"),
    HB("HB"),
    @XmlEnumValue("HBTmf")
    HB_TMF("HBTmf"),
    @XmlEnumValue("Hib")
    HIB("Hib"),
    @XmlEnumValue("Inf")
    INF("Inf"),
    IPV("IPV"),
    JE("JE"),
    @XmlEnumValue("Men")
    MEN("Men"),
    @XmlEnumValue("Men B")
    MEN_B("Men B"),
    @XmlEnumValue("Men-C")
    MEN_C("Men-C"),
    @XmlEnumValue("Men-P-AC")
    MEN_P_AC("Men-P-AC"),
    @XmlEnumValue("Men-P-ACWY")
    MEN_P_ACWY("Men-P-ACWY"),
    MMR("MMR"),
    MR("MR"),
    @XmlEnumValue("Pneu")
    PNEU("Pneu"),
    @XmlEnumValue("Pneu-C-7")
    PNEU_C_7("Pneu-C-7"),
    @XmlEnumValue("Pneu-P-23")
    PNEU_P_23("Pneu-P-23"),
    @XmlEnumValue("Rab")
    RAB("Rab"),
    T("T"),
    @XmlEnumValue("Td")
    TD("Td"),
    @XmlEnumValue("Tdap")
    TDAP("Tdap"),
    @XmlEnumValue("TdapIPV")
    TDAP_IPV("TdapIPV"),
    @XmlEnumValue("Td-IPV")
    TD_IPV("Td-IPV"),
    TBE("TBE"),
    @XmlEnumValue("Typh")
    TYPH("Typh"),
    @XmlEnumValue("Typh (HA)")
    TYPH_HA("Typh (HA)"),
    @XmlEnumValue("Typh-I")
    TYPH_I("Typh-I"),
    @XmlEnumValue("Typh-O")
    TYPH_O("Typh-O"),
    @XmlEnumValue("Var")
    VAR("Var"),
    YF("YF"),
    HPV("HPV"),
    @XmlEnumValue("MMR-Var")
    MMR_VAR("MMR-Var"),
    ROT("ROT"),
    @XmlEnumValue("Zos")
    ZOS("Zos"),
    @XmlEnumValue("BAtx")
    B_ATX("BAtx"),
    @XmlEnumValue("CMVIg")
    CMV_IG("CMVIg"),
    @XmlEnumValue("DAtx")
    D_ATX("DAtx"),
    @XmlEnumValue("HBIg")
    HB_IG("HBIg"),
    @XmlEnumValue("Ig")
    IG("Ig"),
    @XmlEnumValue("RabIg")
    RAB_IG("RabIg"),
    @XmlEnumValue("RSVAb")
    RSV_AB("RSVAb"),
    @XmlEnumValue("RSVIg")
    RSV_IG("RSVIg"),
    @XmlEnumValue("TIg")
    T_IG("TIg"),
    @XmlEnumValue("VarIg")
    VAR_IG("VarIg"),
    VIG("VIG");
    private final String value;

    ImmunizationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ImmunizationType fromValue(String v) {
        for (ImmunizationType c: ImmunizationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
