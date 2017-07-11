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


package oscar.oscarWaitingList.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.oscarehr.common.dao.WaitingListNameDao;
import org.oscarehr.common.model.WaitingListName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WLWaitingListNameBeanHandler {
	
	@Autowired
	private WaitingListNameDao waitingListNameDao;
    
    @Transactional
    public List<WLWaitingListNameBean> getWaitingListNames(){
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	List<WLWaitingListNameBean> waitingListNameList = new ArrayList<WLWaitingListNameBean>();
    	
        List<WaitingListName> wlNames = waitingListNameDao.findActiveWatingListNames();
    	for(WaitingListName tmp:wlNames) {
    		WLWaitingListNameBean wLBean = new WLWaitingListNameBean(String.valueOf(tmp.getId()), tmp.getName(), tmp.getGroupNo(), tmp.getProviderNo(), formatter.format(tmp.getCreateDate()));                   
    		waitingListNameList.add(wLBean);
    	}
        return waitingListNameList;
    }
}
