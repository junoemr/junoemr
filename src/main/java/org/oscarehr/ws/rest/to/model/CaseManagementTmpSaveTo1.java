/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.ws.rest.to.model;

import java.util.Date;

public class CaseManagementTmpSaveTo1
{
    private Integer id;
    private Integer demographicNo;
    private String providerNo;
    private Integer programId;
    private String note;
    private Date updateDate;
    private Integer noteId;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getDemographicNo()
    {
        return demographicNo;
    }

    public void setDemographicNo(Integer demographicNo)
    {
        this.demographicNo = demographicNo;
    }

    public String getProviderNo()
    {
        return providerNo;
    }

    public void setProviderNo(String providerNo)
    {
        this.providerNo = providerNo;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    public void setProgramId(Integer programId)
    {
        this.programId = programId;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public Integer getNoteId()
    {
        return noteId;
    }

    public void setNoteId(Integer noteId)
    {
        this.noteId = noteId;
    }
}
