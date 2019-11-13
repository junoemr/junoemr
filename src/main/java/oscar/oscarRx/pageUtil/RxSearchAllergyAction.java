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


package oscar.oscarRx.pageUtil;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.form.pharmaForms.formBPMH.util.JsonUtil;
import oscar.oscarRx.util.RxDrugRef;

public final class RxSearchAllergyAction extends Action {
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String providerNo = loggedInInfo.getLoggedInProviderNo();
        securityInfoManager.requireOnePrivilege(providerNo, SecurityInfoManager.READ, null, "_allergy");

        // Setup variables
        // execute search

        RxSearchAllergyForm frm = (RxSearchAllergyForm)form;
        // JSON overrides the form.
        String jsonData = request.getParameter("jsonData");
        if (jsonData != null)
        {
            frm = (RxSearchAllergyForm)JsonUtil.jsonToPojo(jsonData, RxSearchAllergyForm.class);
        }

        // Search a drug like another one
        RxDrugRef drugRef = new RxDrugRef();

        Vector vec;
        Vector<String> catVec = new Vector<>();

        /**
         * <html:checkbox property="type4" /> Drug Classes   9 |  4 | ther_class
         * <html:checkbox property="type3" /> Ingredients   13 |  6 | ingredient
         * <html:checkbox property="type2" /> Generic Names  6 |  1 | generic
         * <html:checkbox property="type1" /> Brand Names    8 |  3 | brandname
         *
         *|  8 | anatomical class
         *|  9 | chemical class
         *| 10 | therapeutic class
         *| 11 | generic
         *| 12 | composite generic
         *| 13 | branded product
         *| 14 | ingredient

         *
         *
         **/

        if (frm.getTypeBrandName())
        {
            catVec.add(Integer.toString(RxSearchAllergyForm.BRANDED_PRODUCT));
        }

        if (frm.getTypeGenericName())
        {
            catVec.add(Integer.toString(RxSearchAllergyForm.GENERIC));
            catVec.add(Integer.toString(RxSearchAllergyForm.COMPOSITE_GENERIC));
        }

        if (frm.getTypeIngredient()){
            catVec.add(Integer.toString(RxSearchAllergyForm.INGREDIENT));
        }

        if (frm.getTypeDrugClass()){
            catVec.add(Integer.toString(RxSearchAllergyForm.ANATOMICAL));
            catVec.add(Integer.toString(RxSearchAllergyForm.THERAPEUTIC));

        }

        boolean itemsFound = true;

        String wildcardRightOnly = OscarProperties.getInstance().getProperty("allergies.search_right_wildcard_only", "false");
        vec = drugRef.list_search_element_select_categories(frm.getSearchString(), catVec, Boolean.valueOf(wildcardRightOnly));

        //  'id':'0','category':'','name'
        Allergy[] allergies = new Allergy[vec == null ? 0:vec.size()];

        String includeClassesStr = OscarProperties.getInstance().getProperty("allergies.include_ahfs_class_in_results", "true");
        boolean includeClasses = Boolean.valueOf(includeClassesStr);

        TreeMap<String, Allergy> flatList = new TreeMap<>();

        //we want to categorize the search results.
        Map<Integer, List<Allergy>> allergyResults = new HashMap<>();
        allergyResults.put(RxSearchAllergyForm.ANATOMICAL, new ArrayList<>());
        allergyResults.put(RxSearchAllergyForm.CHEMICAL, new ArrayList<>());
        allergyResults.put(RxSearchAllergyForm.THERAPEUTIC, new ArrayList<>());
        allergyResults.put(RxSearchAllergyForm.GENERIC, new ArrayList<>());
        allergyResults.put(RxSearchAllergyForm.COMPOSITE_GENERIC, new ArrayList<>());
        allergyResults.put(RxSearchAllergyForm.BRANDED_PRODUCT, new ArrayList<>());
        allergyResults.put(RxSearchAllergyForm.INGREDIENT, new ArrayList<>());

        Vector<String> classVec = new Vector<>();

        // Vec may still be null at this point (error occurred when attempting to instantiate it earlier)
        if (vec != null)
        {
            for (int i = 0; i < vec.size(); i++)
            {
                Hashtable hash = (Hashtable) vec.get(i);
                String name = (String)hash.get("name");
                // If the search result for that category turns up nothing, this is blank
                if ("".equals(hash.get("category")))
                {
                    continue;
                }
                int typeCode = (Integer)hash.get("category");
                String id = String.valueOf(hash.get("id"));

                if (!name.equals("None found") && allergies != null && allergies.length > 0)
                {
                    allergies[i] = new Allergy();

                    allergies[i].setTypeCode(typeCode);
                    allergies[i].setDrugrefId(id);
                    allergies[i].setDescription(name);

                    if (allergies[i].getTypeCode() == RxSearchAllergyForm.BRANDED_PRODUCT)
                    {
                        classVec.add("" + allergies[i].getDrugrefId());
                    }

                    String listKey = allergies[i].getDescription();
                    allergyResults.get(typeCode).add(allergies[i]);

                    boolean inFlatList = false;
                    for (Allergy allergy : flatList.values())
                    {
                        if (allergy.getDescription().equals(listKey))
                        {
                            inFlatList = true;
                        }
                    }

                    if (!inFlatList && listKey != null)
                    {
                        flatList.put(listKey, allergies[i]);
                    }
                }
                else
                {
                    itemsFound = false;
                    allergies = null;
                }

            }
        }

        Hashtable returnHash = new Hashtable();

        if (itemsFound && includeClasses && classVec.size() > 0)
        {
            Vector classVec2 = drugRef.list_drug_class(classVec);

            if (classVec2 != null)
            {
                Hashtable hash;
                Vector strVec;

                for (int j = 0; j < classVec2.size(); j++)
                {
                    hash = (java.util.Hashtable) classVec2.get(j);
                    //'id_drug'     'id_class'      'name'
                    String idDrug  = String.valueOf(hash.get("id_drug"));
                    String idClass = String.valueOf(hash.get("id_class"));
                    String name    = String.valueOf(hash.get("name"));
                    String[] strArr = new String[2];
                    strArr[0] = idClass;
                    strArr[1] = name;
                    MiscUtils.getLogger().debug(j+" idDrug "+idDrug+" idClass "+idClass+" name "+name);

                    if (returnHash.containsKey(idDrug))
                    {
                        strVec = (Vector) returnHash.get(idDrug);
                        strVec.add(strArr);
                        returnHash.put(idDrug, strVec);
                    }
                    else
                    {
                        strVec = new Vector();
                        strVec.add(strArr);
                        returnHash.put(idDrug,strVec);
                    }
                }
            }
        }

        if (allergies != null && allergies.length > 0)
        {
        	request.setAttribute("allergyResults", allergyResults);
            request.setAttribute("allergies", allergies);
            request.setAttribute("drugClasses", returnHash);
            request.setAttribute("flatMap", flatList);
        }

        return mapping.findForward("success");
    }
}
