/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.oscarEncounter.oscarMeasurements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.oscarehr.measurements.model.FlowSheetUserCreated;
import org.oscarehr.measurements.model.Flowsheet;
import org.oscarehr.util.MiscUtils;

import org.springframework.beans.factory.InitializingBean;

import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypeBeanHandler;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypesBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctValidationsBean;
import oscar.oscarEncounter.oscarMeasurements.data.ExportMeasurementType;
import oscar.oscarEncounter.oscarMeasurements.data.ImportMeasurementTypes;
import oscar.oscarEncounter.oscarMeasurements.util.DSCondition;
import oscar.oscarEncounter.oscarMeasurements.util.Recommendation;
import oscar.oscarEncounter.oscarMeasurements.util.RuleBaseCreator;
import oscar.oscarEncounter.oscarMeasurements.util.TargetColour;

/**
 * @author jay
 */
public class MeasurementTemplateFlowSheetConfig implements InitializingBean
{

	private static Logger log = MiscUtils.getLogger();

	private SAXBuilder saxBuilder = new SAXBuilder();

	private List<File> systemFlowsheetFiles;

	private List<String> dxTriggers = new ArrayList<>();
	private List<String> programTriggers = new ArrayList<>();
	private List<String> universalFlowSheets = new ArrayList<>();

	private Hashtable<String, List<String>> dxTrigHash = new Hashtable<>();
	private List<MeasurementFlowSheet> flowsheetTemplates = new ArrayList<>();
	private Hashtable<String, String> flowsheetDisplayNames = new Hashtable<>();

	private static MeasurementTemplateFlowSheetConfig measurementTemplateFlowSheetConfig;

	private HashMap<String, List<String>> programTrigHash = new HashMap<>();
	private List<Flowsheet> systemFlowsheets = new ArrayList<>();
	private List<FlowSheetUserCreated> userCreatedFlowsheets = new ArrayList<>();
	private List<Flowsheet> databaseFlowsheets = new ArrayList<>();

    public void afterPropertiesSet() throws Exception {
        measurementTemplateFlowSheetConfig = this;
    }

    /**
     * Creates a new instance of MeasurementTemplateFlowSheetConfig
     */
    private MeasurementTemplateFlowSheetConfig() {
    }

	public static MeasurementTemplateFlowSheetConfig getInstance()
	{
		return measurementTemplateFlowSheetConfig;
	}

	public int getNumCachedFlowsheets()
	{
		return flowsheetTemplates.size();
	}

	public void cacheFlowsheetTemplate(String name, MeasurementFlowSheet flowSheet)
	{
		flowsheetTemplates.add(flowSheet);
		flowsheetDisplayNames.put(name, flowSheet.getDisplayName());
		addTriggers(flowSheet.getDxTriggers(), name);
		setupFlowsheetTriggers(flowSheet);
	}

	public void cacheSystemFlowsheet(Flowsheet systemFlowsheet, MeasurementFlowSheet flowsheetTemplate)
	{
		cacheFlowsheetTemplate(flowsheetTemplate.getName(), flowsheetTemplate);
		systemFlowsheet.setDisplayName(flowsheetTemplate.getDisplayName());
		systemFlowsheets.add(systemFlowsheet);
	}

	public void cacheUserCreatedFlowsheet(FlowSheetUserCreated flowSheetUserCreated, MeasurementFlowSheet template)
	{
		cacheFlowsheetTemplate(template.getName(), template);
		userCreatedFlowsheets.add(flowSheetUserCreated);
	}

	public void cacheDatabaseFlowsheet(Flowsheet databaseFlowsheet, MeasurementFlowSheet template)
	{
		cacheFlowsheetTemplate(template.getName(), template);
		databaseFlowsheet.setDisplayName(template.getDisplayName());
		databaseFlowsheets.add(databaseFlowsheet);
	}

	public void clearCache()
	{
		dxTriggers = new ArrayList<>();
		programTriggers = new ArrayList<>();
		dxTrigHash = new Hashtable<>();
		programTrigHash = new HashMap<>();
		flowsheetDisplayNames = new Hashtable<>();
		universalFlowSheets = new ArrayList<>();
		databaseFlowsheets = new ArrayList<>();
		flowsheetTemplates = new ArrayList<>();
		systemFlowsheets = new ArrayList<>();
		userCreatedFlowsheets = new ArrayList<>();
	}

	/**
	 * Given a flowsheet name and its database entity, update our cached list with any new information.
	 * Note that the flowsheet's display name is transient. If we find an entry to update, we have
	 * to copy the display name from the old entry into the entry being cached.
	 * @param name name of the old flowsheet entry to update
	 * @param flowsheet Flowsheet entry to replace the old cached version with
	 */
	public void updateCache(String name, Flowsheet flowsheet)
	{
		for (Flowsheet cachedFlowsheet : systemFlowsheets)
		{
			if (cachedFlowsheet.getName() != null && cachedFlowsheet.getName().equals(name))
			{
				systemFlowsheets.remove(cachedFlowsheet);
				flowsheet.setDisplayName(cachedFlowsheet.getDisplayName());
				systemFlowsheets.add(flowsheet);
				return;
			}
		}

		for (Flowsheet cachedFlowsheet : databaseFlowsheets)
		{
			if (cachedFlowsheet.getName() != null && cachedFlowsheet.getName().equals(name))
			{
				databaseFlowsheets.remove(cachedFlowsheet);
				flowsheet.setDisplayName(cachedFlowsheet.getDisplayName());
				databaseFlowsheets.add(flowsheet);
				return;
			}
		}
		MiscUtils.getLogger().warn("Couldn't find flowsheet to remove based off: " + name);
	}

	public void updateCache(String name, FlowSheetUserCreated flowSheetUserCreated)
	{
		for (FlowSheetUserCreated cachedFlowsheet : userCreatedFlowsheets)
		{
			if (cachedFlowsheet.getName() != null && cachedFlowsheet.getName().equals(name))
			{
				userCreatedFlowsheets.remove(cachedFlowsheet);
				userCreatedFlowsheets.add(flowSheetUserCreated);
				return;
			}
		}
	}

	public List<MeasurementFlowSheet> getFlowsheetTemplates()
	{
		return flowsheetTemplates;
	}

	public List<String> getUniversalFlowSheets()
	{
		return universalFlowSheets;
	}

	public String getDisplayName(String name) {
		return flowsheetDisplayNames.get(name);
	}

	public Hashtable<String, String> getFlowsheetDisplayNames(){
		return flowsheetDisplayNames;
	}

	public List<String> getDxTriggers()
	{
		return dxTriggers;
	}

	public List<String> getProgramTriggers()
	{
		return programTriggers;
	}

	public List<Flowsheet> getSystemFlowsheets()
	{
		return systemFlowsheets;
	}

	public List<FlowSheetUserCreated> getUserCreatedFlowsheets()
	{
		return userCreatedFlowsheets;
	}

	public List<Flowsheet> getDatabaseFlowsheets()
	{
		return databaseFlowsheets;
	}

	public List<String> getFlowsheetForDxCode(String code)
	{
		return dxTrigHash.get(code);
	}

	public List<String> getFlowsheetForProgramId(String code)
	{
		return programTrigHash.get(code);
	}

	public List<File> getSystemFlowsheetFiles()
	{
		return systemFlowsheetFiles;
	}

	public void setSystemFlowsheetFiles(List<File> systemFlowsheetFiles)
	{
		this.systemFlowsheetFiles = systemFlowsheetFiles;
	}

	/**
	 * Given a flowsheet, determine whether it is universally available or whether it needs drug or program triggers.
	 * @param measurementFlowSheet flowsheet to possibly set triggers for
	 */
	public void setupFlowsheetTriggers(MeasurementFlowSheet measurementFlowSheet)
	{
		String[] dxTriggers = measurementFlowSheet.getDxTriggers();
		String[] programTriggers = measurementFlowSheet.getProgramTriggers();
		if (measurementFlowSheet.isUniversal() && !universalFlowSheets.contains(measurementFlowSheet.getName()))
		{
			universalFlowSheets.add(measurementFlowSheet.getName());
		}
		else if (dxTriggers != null && dxTriggers.length > 0)
		{
			addTriggers(dxTriggers, measurementFlowSheet.getName());
		}
		else if (programTriggers != null && programTriggers.length > 0)
		{
			addProgramTriggers(programTriggers, measurementFlowSheet.getName());
		}

	}

    private void addTriggers(String[] dxTrig, String name) {
        if (dxTrig != null) {
            for (String aDxTrig : dxTrig) {
                if (!dxTriggers.contains(aDxTrig)) {
                    dxTriggers.add(aDxTrig);
                }
                if (dxTrigHash.containsKey(aDxTrig)) {
                    List<String> l = dxTrigHash.get(aDxTrig);
                    if (!l.contains(name)) {
                        l.add(name);
                    }
                } else {
                    ArrayList<String> l = new ArrayList<String>();
                    l.add(name);
                    dxTrigHash.put(aDxTrig, l);
                }
            }
        }
    }

    private void addProgramTriggers(String[] programTrig, String name) {
        if (programTrig != null) {
            for (String aProgramTrig : programTrig) {
                if (!programTriggers.contains(aProgramTrig)) {
                    programTriggers.add(aProgramTrig);
                }
                if (programTrigHash.containsKey(aProgramTrig)) {
                    List<String> l = programTrigHash.get(aProgramTrig);
                    if (!l.contains(name)) {
                        l.add(name);
                    }
                } else {
                    ArrayList<String> l = new ArrayList<String>();
                    l.add(name);
                    programTrigHash.put(aProgramTrig, l);
                }
            }
        }
    }

    private void processItems(List<Element> elements, List<Node> aLevels, Node parent, MeasurementFlowSheet mFlowSheet ) {
        for( Element e: elements) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            List<Attribute> attr = e.getAttributes();
            for (Attribute att : attr) {
                h.put(att.getName(), att.getValue());
            }
            FlowSheetItem item = new FlowSheetItem(h);
            Node node = new Node();
            node.flowSheetItem = item;
            node.parent = parent;
            if( e.getName().equalsIgnoreCase("header") ) {
                List<Element>children = e.getChildren();
                node.children = new ArrayList<Node>();
                aLevels.add(node);
                processItems(children,node.children, node, mFlowSheet);
            }
            else if( e.getName().equalsIgnoreCase("item") ) {

            	String item_type = h.get("measurement_type");
            	if (item_type==null) item_type = h.get("prevention_type");
            	
                if (item_type != null) {

                    log.debug("ADDING " + item_type);

                    int ruleCount = 0;
                    Element rules  = e.getChild("rules");
                    if (rules !=null){
                       List<Element> recomends = rules.getChildren("recommendation");
                       List<Recommendation> ds = new ArrayList<Recommendation>();
                       for(Element reco: recomends){
                           ruleCount++;
                            ds.add(new Recommendation(reco, item_type+ruleCount, item_type));
                       }
                       MiscUtils.getLogger().debug(""+ item_type+ " adding ds  "+ds);
                       item.setRecommendations(ds);
                    }
                    Element rulesets = e.getChild("ruleset");
                    List<TargetColour> rs = new ArrayList<TargetColour>();
                    if (rulesets != null){
                        List<Element> rulez = rulesets.getChildren("rule");
                        if (rulez != null){
                            for(Element r: rulez){
                                rs.add(new TargetColour(r));
                            }
                        }

                    }

                    log.debug(" meas "+item_type+"  size "+rs.size());

                    if (rs.size() > 0){
                        item.setTargetColour(rs);
                    }

                }

                item = mFlowSheet.addListItem(item);
                node.flowSheetItem = item;
                aLevels.add(node);
            }
        }

    }

    private void processMeasurementTypes(List<Element> elements, Node parent, MeasurementFlowSheet mFlowSheet)
    {
        Hashtable<String, String> flowsheetMeasurements = new Hashtable<String, String>();
        for( Element e: elements)
        {
            List<Attribute> attributes = e.getAttributes();
            for (Attribute att : attributes) {
                flowsheetMeasurements.put(att.getName(), att.getValue());
            }

            EctMeasurementTypesBean measurements = new EctMeasurementTypesBean(flowsheetMeasurements);

            Node node = new Node();
            node.parent = parent;

            Element validations  = e.getChild("validationRule");
            if (validations !=null)
            {
                EctValidationsBean vb = new EctValidationsBean();
                vb.setName(validations.getAttributeValue("name"));
                vb.setMaxValue(validations.getAttributeValue("maxValue"));
                vb.setMinValue(validations.getAttributeValue("minValue"));
                vb.setIsDate(validations.getAttributeValue("isDate"));
                vb.setIsNumeric(validations.getAttributeValue("isNumeric"));
                vb.setRegularExp(validations.getAttributeValue("regularExp"));
                vb.setMaxLength(validations.getAttributeValue("maxLength"));
                vb.setMinLength(validations.getAttributeValue("minLength"));

                measurements.addValidationRule(vb);
            }
            measurements = mFlowSheet.addMeasurements(measurements);
            node.flowSheetMeasurement = measurements;
        }
    }

    public class Node {
        public Node parent;
        public List<Node> children;
        public FlowSheetItem flowSheetItem;
        public EctMeasurementTypesBean flowSheetMeasurement;
        public int numSibling = -1;

        public Node getFirstChild() {
            if( children != null && children.size() > 0 ) {
                numSibling = 0;
                return children.get(numSibling);
            }

            return null;
        }

        public Node getNextSibling() {

            if( parent != null) {
                ++parent.numSibling;
                if( parent.numSibling < parent.children.size() ) {
                    return parent.children.get(parent.numSibling);
                }
            }

            return null;
        }

        public boolean hasNextSibling() {
            if( parent == null ) {
                return false;
            }

            return (parent.numSibling < parent.children.size()-1);
        }
    }

    public MeasurementFlowSheet createflowsheet(InputStream is) {
        MeasurementFlowSheet d = new MeasurementFlowSheet();

        try {
            Document doc = saxBuilder.build(is);
            Element root = doc.getRootElement();

            ///


            ///
            //MAKE SURE ALL MEASUREMENTS HAVE BEEN INITIALIZED
            ImportMeasurementTypes importMeasurementTypes = new ImportMeasurementTypes();
            importMeasurementTypes.importMeasurements(root);

            List<Element> measurements = root.getChildren("measurement");

            processMeasurementTypes(measurements, null, d);

            List indi = root.getChildren("indicator"); // key="LOW" colour="blue">
            for (int i = 0; i < indi.size(); i++) {
                Element e = (Element) indi.get(i);
                d.AddIndicator(e.getAttributeValue("key"), e.getAttributeValue("colour"));
            }
            List<Element> elements = root.getChildren();
            List<Node> aItems = new ArrayList<Node>();

            processItems(elements, aItems, null, d);
            d.setItemHeirarchy(aItems);

            if (root.getAttribute("name") != null) {
                d.setName(root.getAttribute("name").getValue());
            }
            if (root.getAttribute("display_name") != null) {
                d.setDisplayName(root.getAttribute("display_name").getValue());
            }

            if (root.getAttribute("top_HTML") != null) {
                d.setTopHTMLFileName(root.getAttribute("top_HTML").getValue());
            }

            if (root.getAttribute("ds_rules") != null && root.getAttribute("ds_rules").getValue().length()>0 ) {
                d.loadRuleBase(root.getAttribute("ds_rules").getValue());
            }
            if (root.getAttribute("dxcode_triggers") != null) {
                d.parseDxTriggers(root.getAttribute("dxcode_triggers").getValue());
            }

            if (root.getAttribute("program_triggers") != null) {
                d.parseProgramTriggers(root.getAttribute("program_triggers").getValue());
            }

            if (root.getAttribute("warning_colour") != null) {
                d.setWarningColour(root.getAttribute("warning_colour").getValue());
            }
            if (root.getAttribute("recommendation_colour") != null) {
                d.setRecommendationColour(root.getAttribute("recommendation_colour").getValue());
            }
            if (root.getAttribute("is_universal") != null) {
                d.setUniversal("true".equals(root.getAttribute("is_universal").getValue()));
            }
            if (root.getAttribute("is_medical") != null) {
                d.setMedical("true".equals(root.getAttribute("is_medical").getValue()));
            }

        } catch (Exception e) {
            MiscUtils.getLogger().error("Error", e);
        }

        d.loadRuleBase();
        return d;
    }

    public MeasurementFlowSheet validateFlowsheet(String data) {
    	InputStream is = null;
    	try {
             is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            MiscUtils.getLogger().error("error",e);
            return null;
        }

        MeasurementFlowSheet d = new MeasurementFlowSheet();

        try {
            Document doc = saxBuilder.build(is);
            Element root = doc.getRootElement();


            //MAKE SURE ALL MEASUREMENTS HAVE BEEN INITIALIZED
            ImportMeasurementTypes importMeasurementTypes = new ImportMeasurementTypes();
            importMeasurementTypes.importMeasurements(root);

            List<Element> measurements = root.getChildren("measurement");

            processMeasurementTypes(measurements, null, d);

            List indi = root.getChildren("indicator"); // key="LOW" colour="blue">
            for (int i = 0; i < indi.size(); i++) {
                Element e = (Element) indi.get(i);
                d.AddIndicator(e.getAttributeValue("key"), e.getAttributeValue("colour"));
            }
            List<Element> elements = root.getChildren();
            List<Node> aItems = new ArrayList<Node>();

                processItems(elements, aItems, null, d);
                d.setItemHeirarchy(aItems);

            if (root.getAttribute("name") != null) {
                d.setName(root.getAttribute("name").getValue());
            }
            if (root.getAttribute("display_name") != null) {
                d.setDisplayName(root.getAttribute("display_name").getValue());
            }

            if (root.getAttribute("top_HTML") != null) {
                d.setTopHTMLFileName(root.getAttribute("top_HTML").getValue());
            }

            if (root.getAttribute("ds_rules") != null) {
                d.loadRuleBase(root.getAttribute("ds_rules").getValue());
            }
            if (root.getAttribute("dxcode_triggers") != null) {
                d.parseDxTriggers(root.getAttribute("dxcode_triggers").getValue());
            }

            if (root.getAttribute("program_triggers") != null) {
                d.parseProgramTriggers(root.getAttribute("program_triggers").getValue());
            }

            if (root.getAttribute("warning_colour") != null) {
                d.setWarningColour(root.getAttribute("warning_colour").getValue());
            }
            if (root.getAttribute("recommendation_colour") != null) {
                d.setRecommendationColour(root.getAttribute("recommendation_colour").getValue());
            }
            if (root.getAttribute("is_universal") != null) {
                d.setUniversal("true".equals(root.getAttribute("is_universal").getValue()));
            }
            if (root.getAttribute("is_medical") != null) {
                d.setMedical("true".equals(root.getAttribute("is_medical").getValue()));
            }

        } catch (Exception e) {
            MiscUtils.getLogger().error("error",e);
            return null;
        }

        d.loadRuleBase();
        return d;
    }

    protected Element getRuleBaseElement(String ruleName,String measurement,Hashtable<String,String> recowarn){

        log.debug("LOADING RULES - getRuleBaseElement");
                    ArrayList<DSCondition> list = new ArrayList<DSCondition>();
        String toParse = recowarn.get("monthrange");
        String consequenceType = "Recommendation";
        if( recowarn.get("strength") != null){
            if (recowarn.get("strength").equals("warning")){
                consequenceType = "Warning";
            }
        }
        String consequence = "";

        String NUMMONTHS = "\"+m.getLastDateRecordedInMonths(\""+measurement+"\")+\"";


            if (toParse.indexOf("-") != -1 && toParse.indexOf("-") != 0 ){ //between style
                String[] betweenVals = toParse.split("-");
                if (betweenVals.length == 2 ){
                    //int lower = Integer.parseInt(betweenVals[0]);
                    //int upper = Integer.parseInt(betweenVals[1]);
                    list.add(new DSCondition("getLastDateRecordedInMonths", measurement, ">=", betweenVals[0]));
                    list.add(new DSCondition("getLastDateRecordedInMonths", measurement, "<=", betweenVals[1]));
                    if ( recowarn.get("text") == null){
                         consequence ="m.add"+consequenceType+"(\""+measurement+"\",\""+measurement+"\" 1 hasn't been reviewed in \"+m.getLastDateRecordedInMonths(\""+measurement+"\")+\" months\";";
                    }
                }

            }else if (toParse.indexOf("&gt;") != -1 ||  toParse.indexOf(">") != -1 ){ // greater than style
                toParse = toParse.replaceFirst("&gt;","");
                toParse = toParse.replaceFirst(">","");

                int gt = Integer.parseInt(toParse);

                list.add(new DSCondition("getLastDateRecordedInMonths", measurement, ">", ""+gt));
                if ( recowarn.get("text") == null){
                         consequence ="m.add"+consequenceType+"(\""+measurement+"\",\""+measurement+"\" 2 hasn't been reviewed in \"+m.getLastDateRecordedInMonths(\""+measurement+"\")+\" months\";";
                }
            }else if (toParse.indexOf("&lt;") != -1  ||  toParse.indexOf("<") != -1 ){ // less than style
                toParse = toParse.replaceFirst("&lt;","");
                toParse = toParse.replaceFirst("<","");

                int lt = Integer.parseInt(toParse);
                list.add(new DSCondition("getLastDateRecordedInMonths", measurement, "<=", ""+lt));
                if ( recowarn.get("text") == null){
                         consequence ="m.add"+consequenceType+"(\""+measurement+"\",\""+measurement+"\" 3 hasn't been reviewed in \"+m.getLastDateRecordedInMonths(\""+measurement+"\")+\" months\";";
                    }

            }else if (!toParse.equals("")){ // less than style
                int eq = Integer.parseInt(toParse);
                list.add(new DSCondition("getLastDateRecordedInMonths", measurement, "==", ""+eq));
                if ( recowarn.get("text") == null){
                         consequence ="m.add"+consequenceType+"(\""+measurement+"\",\""+measurement+"\" 4 hasn'taaaaa been reviewed in \"+m.getLastDateRecordedInMonths(\""+measurement+"\")+\" months\";";
                    }
            }

        if ( recowarn.get("text") != null){
            String txt = recowarn.get("text");
            log.debug("TRY TO REPLACE $NUMMONTHS:"+txt.indexOf("$NUMMONTHS")+" WITH "+NUMMONTHS+  " "+txt);

            txt = txt.replaceAll("\\$NUMMONTHS", NUMMONTHS);
            log.debug("TEXT "+txt);
            consequence ="m.add"+consequenceType+"(\""+measurement+"\",\""+txt+"\");";
            //consequence ="MiscUtils.getLogger().debug(\"HAPPY TO BE WORKING\");";

        }

        RuleBaseCreator rcb = new RuleBaseCreator();
        Element ruleElement = rcb.getRule(ruleName, "oscar.oscarEncounter.oscarMeasurements.MeasurementInfo", list,  consequence) ;


        return ruleElement;
    }

	//This could be used to create the custom on the file flowsheet
	public MeasurementFlowSheet makeNewFlowsheet(MeasurementFlowSheet mFlowsheet) throws IOException
	{
		XMLOutputter outp = new XMLOutputter();
		Element va = getExportFlowsheet( mFlowsheet);

		ByteArrayOutputStream byteArrayout = new ByteArrayOutputStream();
		outp.output(va, byteArrayout);

		InputStream is = new ByteArrayInputStream(byteArrayout.toByteArray());

		EctMeasurementTypeBeanHandler mType = new EctMeasurementTypeBeanHandler();
		return createflowsheet(is);

	}

    public FlowSheetItem getItemFromString(String s)
	{
		FlowSheetItem item = null;
		try
		{
			Document doc = saxBuilder.build(new StringReader(s));
			Element root = doc.getRootElement();

			List<Attribute> attr = root.getAttributes();

			Map<String, String> h = new Hashtable<String, String>();
			for (Attribute att : attr) {
				h.put(att.getName(), att.getValue());
			}
			item = new FlowSheetItem(h);

			int ruleCount = 0;
			Element rules  = root.getChild("rules");

			if (rules != null)
			{
				List<Element> recomends = rules.getChildren("recommendation");
				List<Recommendation> ds = new ArrayList<Recommendation>();
				for(Element reco: recomends)
				{
					ruleCount++;
					ds.add(new Recommendation(reco,"" + h.get("measurement_type")+ruleCount,"" + h.get("measurement_type")));
				}
				item.setRecommendations(ds);
			}

			Element rulesets = root.getChild("ruleset");
			List<TargetColour> rs = new ArrayList<>();
			if (rulesets != null)
			{
				List<Element> rulez = rulesets.getChildren("rule");
				if (rulez != null)
				{
					for(Element r: rulez)
					{
						rs.add(new TargetColour(r));
					}
				}

			}
			if (rs.size() > 0)
			{
				item.setTargetColour(rs);
			}
		}
		catch (JDOMException | IOException e)
		{
			MiscUtils.getLogger().error("Error when attempting to get item from string: ", e);
		}

		return item;
    }


    public Element getItemFromObject(FlowSheetItem fsi){
        Element item = new Element("item");

                    Map h2 = fsi.getAllFields();

                    addAttributeifValueNotNull(item, "prevention_type", fsi.getPreventionType());
                    addAttributeifValueNotNull(item, "measurement_type", fsi.getMeasurementType());
                    addAttributeifValueNotNull(item, "display_name", fsi.getDisplayName());
                    addAttributeifValueNotNull(item, "guideline", fsi.getGuideline());
                    addAttributeifValueNotNull(item, "graphable", (String) h2.get("graphable"));
                    addAttributeifValueNotNull(item, "ds_rules", (String) h2.get("ds_rules"));
                    addAttributeifValueNotNull(item, "value_name", fsi.getValueName());

                    if (h2.get("measurement_type") != null) {
                        log.debug("MEASUREMENT TYPE " + (String) h2.get("measurement_type"));

                        List<Recommendation> dsR = fsi.getRecommendations();
                        log.debug(h2.get("measurement_type") + " LIST DSR " + dsR);
                        if (dsR != null) {
                            Element rules = new Element("rules");
                            for (Recommendation e : dsR) {
                                log.debug("BEFORE ADDING ");
                                rules.addContent(e.getFlowsheetXML());
                                log.debug(rules);
                            }
                            item.addContent(rules);
                        }


                        List<TargetColour> targetColour =fsi.getTargetColour();
                        log.debug("TARGET COLOURS"+targetColour);

                        if (targetColour != null){
                           Element ruleset = new Element("ruleset");

                           for(TargetColour t : targetColour){
                               ruleset.addContent(t.getFlowsheetXML());
                           }
                           item.addContent(ruleset);
                        }
                    }
                    return item;
    }




    public Element getExportFlowsheet(MeasurementFlowSheet mFlowsheet){
            EctMeasurementTypeBeanHandler mType = new EctMeasurementTypeBeanHandler();
            Element va = new Element("flowsheet");


            addAttributeifValueNotNull(va, "name", mFlowsheet.getName());
            addAttributeifValueNotNull(va, "display_name", mFlowsheet.getDisplayName());
            addAttributeifValueNotNull(va, "warning_colour", mFlowsheet.getWarningColour());
            addAttributeifValueNotNull(va, "recommendation_colour", mFlowsheet.getRecommendationColour());
            addAttributeifValueNotNull(va, "top_HTML", mFlowsheet.getTopHTMLStream());
            addAttributeifValueNotNull(va,"dxcode_triggers",mFlowsheet.getDxTriggersString());
            addAttributeifValueNotNull(va,"program_triggers",mFlowsheet.getProgramTriggersString());

            Hashtable indicatorHash = mFlowsheet.getIndicatorHashtable();
            Enumeration enu = indicatorHash.keys();
            while (enu.hasMoreElements()) {
                String key = (String) enu.nextElement();
                Element ind = new Element("indicator");
                addAttributeifValueNotNull(ind, "key", key);
                addAttributeifValueNotNull(ind, "colour", (String) indicatorHash.get(key));
                va.addContent(ind);
            }

            List<String> measurements = mFlowsheet.getMeasurementList();

            log.debug("SET HAS MEASUREMENTS" + measurements);
            int count = 0;
            if (measurements != null) {
                for (String mstring : measurements) {

                    EctMeasurementTypesBean measurementTypesBean = mType.getMeasurementType(mstring);

                    Map h2 = mFlowsheet.getMeasurementFlowSheetInfo(mstring);

                    Element item = new Element("item");


                    addAttributeifValueNotNull(item, "prevention_type", (String) h2.get("prevention_type"));
                    addAttributeifValueNotNull(item, "measurement_type", (String) h2.get("measurement_type"));
                    addAttributeifValueNotNull(item, "display_name", (String) h2.get("display_name"));
                    addAttributeifValueNotNull(item, "guideline", (String) h2.get("guideline"));
                    addAttributeifValueNotNull(item, "graphable", (String) h2.get("graphable"));
                    addAttributeifValueNotNull(item, "value_name", (String) h2.get("value_name"));
                    addAttributeifValueNotNull(item, "ds_rules", (String) h2.get("ds_rules"));
                    if (h2.get("measurement_type") != null) {
                        log.debug("MEASUREMENT TYPE " + (String) h2.get("measurement_type"));

                        List<Recommendation> dsR = mFlowsheet.getDSElements((String) h2.get("measurement_type"));
                        log.debug(h2.get("measurement_type") + " LIST DSR " + dsR);
                        if (dsR != null) {
                            Element rules = new Element("rules");
                            for (Recommendation e : dsR) {
                                log.debug("BEFORE ADDING ");
                                rules.addContent(e.getFlowsheetXML());
                                log.debug(rules);
                            }
                            item.addContent(rules);
                        }

                        FlowSheetItem fsi = mFlowsheet.getFlowSheetItem(mstring);  //TODO-legacy: MOVE THIS UP AND REPLACE THE CODE ABOVE
                        List<TargetColour> targetColour =fsi.getTargetColour();
                        log.debug("TARGET COLOURS"+targetColour);

                        if (targetColour != null){
                           Element ruleset = new Element("ruleset");

                           for(TargetColour t : targetColour){
                               ruleset.addContent(t.getFlowsheetXML());
                           }
                           item.addContent(ruleset);
                        }

                    }

                    va.addContent(item);
                    count++;


                    if (measurementTypesBean != null) {
                        ExportMeasurementType emt = new ExportMeasurementType();
                        Element export = emt.exportElement(measurementTypesBean);
                        va.addContent(export);
                    } else {
                        log.debug("--- not loaded --- " + mstring);
                    }
                }
            }
            return va;
    }

       private void addAttributeifValueNotNull(Element element, String attr, String value) {
        if (value != null) {
            element.setAttribute(attr, value);
        }
    }


}
