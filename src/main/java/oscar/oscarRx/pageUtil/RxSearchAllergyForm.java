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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


public final class RxSearchAllergyForm extends ActionForm {

    public static final int ANATOMICAL = 8;
    public static final int CHEMICAL = 9;
    public static final int THERAPEUTIC = 10;
    public static final int GENERIC = 11;
    public static final int COMPOSITE_GENERIC = 12;
    public static final int BRANDED_PRODUCT = 13;
    public static final int INGREDIENT = 14;

    private String searchString = null;
    private boolean type5 = false;
    // type 4 drug classes
    private boolean typeDrugClass = false;
    // type 3ingredients
    private boolean typeIngredient = false;
    // type 2 generic names
    private boolean typeGenericName = false;
    // type 1 brand names
    private boolean typeBrandName = false;
    
    public String getSearchString() {
        return (this.searchString);
    }
    
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    
    public boolean getType5() {
        return (this.type5);
    }
    public void setType5(boolean RHS) {
        this.type5 = RHS;
    }
    
    public boolean getTypeDrugClass() {
        return (this.typeDrugClass);
    }
    public void setTypeDrugClass(boolean RHS) {
        this.typeDrugClass = RHS;
    }
    
    public boolean getTypeIngredient() {
        return (this.typeIngredient);
    }
    public void setTypeIngredient(boolean RHS) {
        this.typeIngredient = RHS;
    }
    
    public boolean getTypeGenericName() {
        return (this.typeGenericName);
    }
    public void setTypeGenericName(boolean RHS) {
        this.typeGenericName = RHS;
    }
    
    public boolean getTypeBrandName() {
        return (this.typeBrandName);
    }
    public void setTypeBrandName(boolean RHS) {
        this.typeBrandName = RHS;
    }
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.searchString = null;
        this.type5 = false;
        this.typeDrugClass = false;
        this.typeIngredient = false;
        this.typeGenericName = false;
        this.typeBrandName = false;
    }
    
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        
        return errors;
        
    }
}
