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


package oscar.oscarDemographic.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.lang.StringEscapeUtils;
import org.oscarehr.util.MiscUtils;

import oscar.oscarDB.DBHandler;

/**
 *
 * @author Jay Gallagher
 */
public class DemographicRelationship {

   /*
   CREATE TABLE relationships (
   id int(10) NOT NULL auto_increment,
   demographic_no int(10) NOT NULL default '0',
   relation_demographic_no int(10) NOT NULL default '0',
   relation varchar(20),
   creation_date datetime default NULL,
   creator varchar(6) NOT NULL default '',
   sub_decision_maker char(1) default '0',
   emergency_contact char(1) default '0',
   notes text,
   deleted char(1) default '0',
   PRIMARY KEY  (id)
   )
   */

   public DemographicRelationship() {
   }

   /**
    * @param facilityId can be null
    */
   public void addDemographicRelationship(String demographic,String linkingDemographic,String relationship,boolean sdm,boolean emergency,String notes,String providerNo, Integer facilityId){
       String sdmStr = "0";
       String eContact = "0";
       if (sdm){
          sdmStr = "1";
       }
       if(emergency){
          eContact = "1";
       }

       try {

            String sql = "insert into relationships (facility_id,demographic_no,relation_demographic_no,relation,sub_decision_maker,emergency_contact,notes,creator,creation_date) values "
            + "("+facilityId+",'"+demographic+"','"+linkingDemographic+"','"+StringEscapeUtils.escapeSql(relationship)+"','"+sdmStr+"','"+eContact+"','"+StringEscapeUtils.escapeSql(notes)+"','"+providerNo+"',now())";
            DBHandler.RunSQL(sql);

        } catch (SQLException e) {
            MiscUtils.getLogger().error("Error", e);
        }
   }

   public void addDemographicRelationships(String demographic,String linkingDemographic,String relationship,boolean sdm,boolean emergency,String notes,String providerNo, Integer facilityId)
   {
        DemographicRelationship demo = new DemographicRelationship();
        demo.addDemographicRelationship(demographic, linkingDemographic, relationship, sdm, emergency, notes, providerNo, facilityId);
        
        // ***** NEW CODE *****
        // Now link in the opposite direction
        // First work out which pairs match up
        // From AddAlternateConcact.jsp
        
        // Relations for the dropdowns should be stored in a table in the database and not hardcoded
        
         /*
         This is better:
          
                Parent
                StepParent
                Child
                Sibling
                Spouse
                Partner
                Grandparent
                Other Relative
                Other
          
         Sex will determine whether it is a brother,
        grandfather, wife, husband or spouse of the same sex
          */
        
        boolean relationset = false;
		String reverseRelation = relationship;
//--- Child
        if ("Mother".equals(relationship)) {
            reverseRelation = "Child";
            relationset = true;
        } else if ("Father".equals(relationship)) {
            reverseRelation = "Child";
            relationset = true;
        } else if ("Parent".equals(relationship) ) {
            reverseRelation = "Child";
            relationset = true;
        }
//--- Parent
        else if ("Daughter".equals(relationship) ) {
            reverseRelation = "Parent";
            relationset = true;
        } else if ("Son".equals(relationship) ) {
            reverseRelation = "Parent";
            relationset = true;
        } else if ("Child".equals(relationship) ) {
            reverseRelation = "Parent";
            relationset = true;
        }
//--- Spouse
        else if ("Wife".equals(relationship) ) {
            reverseRelation = "Spouse";
            relationset = true;
        } else if ("Husband".equals(relationship) ) {
            reverseRelation = "Spouse";
            relationset = true;
        } else if ("Spouse".equals(relationship) ) {
            // reverseRelation = "Spouse";
            relationset = true;
        }
//--- Sibling
        else if ("Brother".equals(relationship) ) {
            reverseRelation = "Sibling";
            relationset = true;
        } else if ("Sister".equals(relationship) ) {
            reverseRelation = "Sibling";
            relationset = true;
        }
        
        else if ("Sibling".equals(relationship) ) {
            // reverseRelation = "Sibling";
            relationset = true;
        }
//--- Partner
        else if ("Partner".equals(relationship) ) {
            // reverseRelation = "Parent";
            relationset = true;
        }
//--- Grandparent
        else if ("Grandfather".equals(relationship) ) {
            reverseRelation = "Grandparent";
            relationset = true;
        }
        
        else if ("Grandmother".equals(relationship) ) {
            reverseRelation = "Grandparent";
            relationset = true;
        }
        
        else if ("Grandparent".equals(relationship) ) {
            // reverseRelation = "Grandparent";
            relationset = true;
        };
        
        if (relationset) {
            
            //now save this
            DemographicRelationship demo2 = new DemographicRelationship();
            demo2.addDemographicRelationship(linkingDemographic, demographic, reverseRelation, sdm, emergency, notes, providerNo, facilityId);
        }
   }

   public void deleteDemographicRelationship(String id){
      try {

         String sql = "update relationships  set deleted = '1' where  id = '"+id+"'";
         DBHandler.RunSQL(sql);
      } catch (SQLException e) {
         MiscUtils.getLogger().error("Error", e);
      }
   }


	public ArrayList<HashMap<String,String>> getDemographicRelationships(String demographic){
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		try {

			ResultSet rs;
			String sql = "select * from relationships where demographic_no = '"+demographic+"' and deleted != '1'";
			rs = DBHandler.GetSQL(sql);
			while(rs.next()){
				HashMap<String,String> h = new HashMap<String,String>();
				h.put("id", oscar.Misc.getString(rs, "id"));
				h.put("demographic_no", oscar.Misc.getString(rs, "relation_demographic_no"));
				h.put("relation", oscar.Misc.getString(rs, "relation"));
				h.put("sub_decision_maker", oscar.Misc.getString(rs, "sub_decision_maker"));
				h.put("emergency_contact", oscar.Misc.getString(rs, "emergency_contact"));
				h.put("notes", oscar.Misc.getString(rs, "notes"));
				list.add(h);
			}
		} catch (SQLException e) {
			MiscUtils.getLogger().error("Error", e);
		}
		return list;
	}


   public ArrayList<Hashtable<String,String>> getDemographicRelationshipsByID(String id){
      ArrayList<Hashtable<String,String>> list = new ArrayList<Hashtable<String,String>>();
      try {

         ResultSet rs;
         String sql = "select * from relationships where id = '"+id+"' and deleted != '1'";
         rs = DBHandler.GetSQL(sql);
         while(rs.next()){
            Hashtable<String,String> h = new Hashtable<String,String>();
            h.put("demographic_no", oscar.Misc.getString(rs, "demographic_no"));
            h.put("relation_demographic_no", oscar.Misc.getString(rs, "relation_demographic_no"));
            h.put("relation", oscar.Misc.getString(rs, "relation"));
            h.put("sub_decision_maker", oscar.Misc.getString(rs, "sub_decision_maker"));
	    h.put("emergency_contact", oscar.Misc.getString(rs, "emergency_contact"));
            h.put("notes", oscar.Misc.getString(rs, "notes"));
            list.add(h);
         }
      } catch (SQLException e) {
         MiscUtils.getLogger().error("Error", e);
      }
      return list;
   }


   public String getSDM(String demographic){
      String sdm = null;
      try {

         ResultSet rs;
         String sql = "select * from relationships where demographic_no = '"+demographic+"' and deleted != '1' and sub_decision_maker = '1'";
         rs = DBHandler.GetSQL(sql);
         if(rs.next()){
            sdm = oscar.Misc.getString(rs, "relation_demographic_no");
         }
      } catch (SQLException e) {
         MiscUtils.getLogger().error("Error", e);
      }
      return sdm;
   }



   public ArrayList<Hashtable<String,Object>> getDemographicRelationshipsWithNamePhone(String demographic_no){
      ArrayList<Hashtable<String,Object>> list = new ArrayList<Hashtable<String,Object>>();
      try {

         ResultSet rs;
         String sql = "select * from relationships where demographic_no = '"+demographic_no+"' and deleted != '1'";
         rs = DBHandler.GetSQL(sql);
         while(rs.next()){
            Hashtable<String,Object> h = new Hashtable<String,Object>();
            String demo = oscar.Misc.getString(rs, "relation_demographic_no");
            DemographicData dd = new DemographicData();
            org.oscarehr.common.model.Demographic demographic = dd.getDemographic(demo);
            h.put("lastName", demographic.getLastName());
            h.put("firstName", demographic.getFirstName());
            h.put("phone", demographic.getPhone());
            h.put("demographicNo", demo);
            h.put("relation", oscar.Misc.getString(rs, "relation"));

            h.put("subDecisionMaker", booleanConverter(oscar.Misc.getString(rs, "sub_decision_maker")));
            h.put("emergencyContact", booleanConverter(oscar.Misc.getString(rs, "emergency_contact")));
            h.put("notes", oscar.Misc.getString(rs, "notes"));
            h.put("age",demographic.getAge());
            list.add(h);
         }
      } catch (SQLException e) {
         MiscUtils.getLogger().error("Error", e);
      }
      return list;
   }

   public ArrayList<Hashtable<String,Object>> getDemographicRelationshipsWithNamePhone(String demographic_no, Integer facilityId){
	      ArrayList<Hashtable<String,Object>> list = new ArrayList<Hashtable<String,Object>>();
	      try {

	         ResultSet rs;
	         String sql = "select * from relationships where demographic_no = '"+demographic_no+"' and deleted != '1'" +
	                      " and facility_id=" + facilityId.toString();
	         rs = DBHandler.GetSQL(sql);
	         while(rs.next()){
	        	 Hashtable<String,Object> h = new Hashtable<String,Object>();
	            String demo = oscar.Misc.getString(rs, "relation_demographic_no");
	            DemographicData dd = new DemographicData();
	            org.oscarehr.common.model.Demographic demographic = dd.getDemographic(demo);
	            h.put("lastName", demographic.getLastName());
	            h.put("firstName", demographic.getFirstName());
	            h.put("phone", demographic.getPhone());
	            h.put("demographicNo", demo);
	            h.put("relation", oscar.Misc.getString(rs, "relation"));

	            h.put("subDecisionMaker", booleanConverter(oscar.Misc.getString(rs, "sub_decision_maker")));
	            h.put("emergencyContact", booleanConverter(oscar.Misc.getString(rs, "emergency_contact")));
	            h.put("notes", oscar.Misc.getString(rs, "notes"));
	            h.put("age",demographic.getAge());
	            list.add(h);
	         }
	      } catch (SQLException e) {
	         MiscUtils.getLogger().error("Error", e);
	      }
	      return list;
	   }

   public static boolean isValidRelationship(String relationship)
   {
  		return DemographicRelationship.getValidRelationships().contains(relationship);
   }

   public static ArrayList<String> getValidRelationships()
   {
	   ArrayList<String> out = new ArrayList<String>();

	   out.add("Mother");
	   out.add("Father");
	   out.add("Father");
	   out.add("Wife");
	   out.add("Husband");
	   out.add("Partner");
	   out.add("Brother");
	   out.add("Sister");
	   out.add("Son");
	   out.add("Daughter");
	   out.add("Aunt");
	   out.add("Uncle");
	   out.add("GrandFather");
	   out.add("GrandMother");
	   out.add("Guardian");
	   out.add("Foster Parent");
	   out.add("Next of Kin");
	   out.add("Administrative Staff");
	   out.add("Care Giver");
	   out.add("Power of Attorney");
	   out.add("Insurance");
	   out.add("Guarantor");
	   out.add("Spouse");
	   out.add("Other");

	   return out;
   }


   private Boolean booleanConverter(String s){
      boolean isTrue = false;
      if (s != null && s.equals("1")){
         isTrue = true;
      }
      return new Boolean(isTrue);
   }

}
