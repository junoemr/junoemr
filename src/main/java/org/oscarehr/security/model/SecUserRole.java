/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.security.model;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * This is the object class that relates to the secUserRole table.
 * Any customizations belong here.
 */
@Data
@Entity
@Table(name = "secUserRole")
public class SecUserRole extends AbstractModel<Integer> implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Transient
    private int hashCode = Integer.MIN_VALUE;// primary key

    @Deprecated
    @Column(name = "role_name")
    private String roleName;

    @Column(name = "provider_no")
    private String providerNo;

    @Column(name = "activeyn")
    private boolean _active;

    @Column(name = "orgcd")
    private String orgCd;

    @Column(name = "lastUpdateDate")
    private Date lastUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sec_role_id")
    private SecRole secRole;


    // constructors
    public SecUserRole()
    {
    }

    @Override
    public Integer getId()
    {
        return id;
    }

    public String getRoleName()
    {
        return this.roleName;
    }

    /**
     * Set the value related to the column: role_name
     * @param _roleName the role_name value
     */
    public void setRoleName (String _roleName) {
        this.roleName = _roleName;
        this.hashCode = Integer.MIN_VALUE;
    }

    public boolean getActive () {
        return this._active;
    }

    /**
     * Set the value related to the column: activeyn
     * @param _active the active value
     */
    public void setActive (boolean _active) {
        this._active = _active;
        this.hashCode = Integer.MIN_VALUE;
    }

    public String getProviderNo () {
        return this.providerNo;
    }

    /**
     * Set the value related to the column: provider_no
     * @param _providerNo the provider_no value
     */
    public void setProviderNo (String _providerNo) {
        this.providerNo = _providerNo;
        this.hashCode = Integer.MIN_VALUE;
    }

    public boolean equals (Object obj) {
        if (null == obj) return false;
        if (!(obj instanceof SecUserRole)) return false;
        else {
            SecUserRole mObj = (SecUserRole) obj;
            if (null != this.getRoleName() && null != mObj.getRoleName()) {
                if (!this.getRoleName().equals(mObj.getRoleName())) {
                    return false;
                }
            }
            else {
                return false;
            }
            if (null != this.getProviderNo() && null != mObj.getProviderNo()) {
                if (!this.getProviderNo().equals(mObj.getProviderNo())) {
                    return false;
                }
            }
            else {
                return false;
            }
            return true;
        }
    }

    public int hashCode () {
        if (Integer.MIN_VALUE == this.hashCode) {
            StringBuilder sb = new StringBuilder();
            if (null != this.getRoleName()) {
                sb.append(this.getRoleName().hashCode());
                sb.append(":");
            }
            else {
                return super.hashCode();
            }
            if (null != this.getProviderNo()) {
                sb.append(this.getProviderNo().hashCode());
                sb.append(":");
            }
            else {
                return super.hashCode();
            }
            this.hashCode = sb.toString().hashCode();
        }
        return this.hashCode;
    }

    public String toString () {
        return super.toString();
    }

    /**
     * @return a csv of the roleNames, null if the list passed in is null., blank if the list passed in is 0 items.
     */
    public static String getRoleNameAsCsv(List<SecUserRole> secUserRoles)
    {
		if (secUserRoles==null) return(null);

		StringBuilder sb = new StringBuilder();

		for (SecUserRole secUserRole : secUserRoles)
		{
			if (sb.length() > 1) sb.append(',');
			sb.append(secUserRole.getRoleName());
		}

		return(sb.toString());
    }

	public String getOrgCd() {
    	return orgCd;
    }

	public void setOrgCd(String orgCd) {
    	this.orgCd = orgCd;
    }

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}


}
