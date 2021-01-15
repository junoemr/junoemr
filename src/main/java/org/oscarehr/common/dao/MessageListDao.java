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

package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.MessageList;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class MessageListDao extends AbstractDao<MessageList> {

	public MessageListDao() {
		super(MessageList.class);
	}
	
	public List<MessageList> findByProviderNoAndMessageNo(String providerNo, Long messageNo) {
		Query query = createQuery("msg", "msg.providerNo = :pno AND msg.message = :msg");
		query.setParameter("pno", providerNo);
		query.setParameter("msg", messageNo);
		return query.getResultList();
	}

	public List<MessageList> findByProviderNoAndLocationNo(String providerNo, Integer remoteLocation) {
		Query query = createQuery("ml", "ml.providerNo = :providerNo and ml.status not like 'del' and ml.remoteLocation = :remoteLocation order by ml.message");
		query.setParameter("providerNo", providerNo);
		query.setParameter("remoteLocation", remoteLocation);
		return query.getResultList();
	}

	public List<MessageList> findByMessage(Long messageNo) {
	    Query query = createQuery("ml", "ml.message = :messageNo");
		query.setParameter("messageNo", messageNo);
		return query.getResultList();
    }

	public List<MessageList> findByProviderAndStatus(String providerNo, String status) {
		Query query = createQuery("ml", "ml.providerNo = :providerNo and ml.status = :status");
		query.setParameter("providerNo", providerNo);
		query.setParameter("status", status);
		return query.getResultList();	    
    }
    
	public List<MessageList> findUnreadByProvider(String providerNo) {
		Query query = createQuery("ml", "ml.providerNo = :providerNo and ml.status ='new'");
		query.setParameter("providerNo", providerNo);
		return query.getResultList();
	}
	
	public int findUnreadByProviderAndAttachedCount(String providerNo) {
		Query query = entityManager.createQuery("select count(l) from MessageList l, MsgDemoMap m where l.providerNo= :providerNo and l.status='new' and l.message=m.messageID");
		
		query.setParameter("providerNo", providerNo);
		return getCountResult(query).intValue();
		
	}
	
	
    public List<MessageList> search(String providerNo, String status, int start, int max) {
    	
    	StringBuilder sql = new StringBuilder();
    	sql.append("select ml from MessageList ml, MessageTbl mt where ml.message = mt.id");
    	
    	if(providerNo != null && !providerNo.isEmpty()) {
    		sql.append(" AND ml.providerNo= :providerNo ");
    	}
    	if(status != null && !status.isEmpty()) {
    		sql.append(" AND ml.status = :status ");
    	}
     
    	sql.append(" ORDER BY mt.date DESC, mt.time DESC");
    	
    	Query query = entityManager.createQuery(sql.toString());
    	
    	if(providerNo != null && !providerNo.isEmpty()) {
    		query.setParameter("providerNo", providerNo);
    	}
    	if(status != null && !status.isEmpty()) {
    		query.setParameter("status", status);
    	}
		query.setFirstResult(start);
		setLimit(query,max);
		List<MessageList> result = query.getResultList();
		
		return result;
	}
        
    public Integer searchAndReturnTotal(String providerNo, String status) {
    	
    	StringBuilder sql = new StringBuilder();
    	sql.append("select count(ml) from MessageList ml, MessageTbl mt where ml.message = mt.id");
    	
    	if(providerNo != null && !providerNo.isEmpty()) {
    		sql.append(" AND ml.providerNo= :providerNo ");
    	}
    	if(status != null && !status.isEmpty()) {
    		sql.append(" AND ml.status = :status ");
    	}
     
    	Query query = entityManager.createQuery(sql.toString());
    	
    	if(providerNo != null && !providerNo.isEmpty()) {
    		query.setParameter("providerNo", providerNo);
    	}
    	if(status != null && !status.isEmpty()) {
    		query.setParameter("status", status);
    	}
		
		Integer result = ((Long)query.getSingleResult()).intValue();
		
		return result;
	}

	/**
	 * Dao call to get total number of corresponding messages.
	 * Note: This should really be split up into three different functions.
	 * @param type type of message to be
	 * @param providerNo requesting provider no
	 * @param remoteLocation location message sent from
	 * @param searchFilter any optional phrases to include
	 * @return number of relevant messages
	 */
	public Integer messagesTotal(int type, String providerNo, Integer remoteLocation, String searchFilter)
	{
		searchFilter = "%" + searchFilter + "%";

		String sql;

		switch (type)
		{
			case 1:
				// sent
				sql = "SELECT COUNT(mt) FROM MessageTbl mt " +
						"WHERE mt.sentByNo= :providerNo " +
					"AND mt.sentByLocation = :remoteLocation " +
					"AND (mt.subject LIKE :filter1 OR mt.message LIKE :filter2 OR mt.sentBy LIKE :filter3 OR mt.sentTo LIKE :filter4)";
				break;
			case 2:
				// deleted
				sql = "SELECT COUNT(mt) FROM MessageTbl mt, MessageList ml " +
						"WHERE ml.status LIKE 'del' AND ml.message = mt.id " +
						"AND ml.providerNo= :providerNo " +
						"AND ml.remoteLocation = :remoteLocation " +
						"AND (mt.subject LIKE :filter1 OR mt.message LIKE :filter2 OR mt.sentBy LIKE :filter3 OR mt.sentTo LIKE :filter4)";
				break;
			default:
				// inbox
				sql = "SELECT COUNT(mt) FROM MessageList ml, MessageTbl mt " +
						"WHERE ml.status != 'del' " +
						"AND ml.message = mt.id " +
						"AND ml.providerNo= :providerNo " +
						"AND ml.remoteLocation = :remoteLocation " +
						"AND (mt.subject LIKE :filter1 OR mt.message LIKE :filter2 OR mt.sentBy LIKE :filter3 OR mt.sentTo LIKE :filter4)";
				break;
		}

		Query query = entityManager.createQuery(sql);

		query.setParameter("providerNo", providerNo);
		query.setParameter("remoteLocation", remoteLocation);

		query.setParameter("filter1", searchFilter);
		query.setParameter("filter2", searchFilter);
		query.setParameter("filter3", searchFilter);
		query.setParameter("filter4", searchFilter);

		return ((Long)query.getSingleResult()).intValue();
	}

}
