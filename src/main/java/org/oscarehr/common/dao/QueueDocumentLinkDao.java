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

import org.oscarehr.common.model.QueueDocumentLink;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jackson bi
 */
@Repository
public class QueueDocumentLinkDao extends AbstractDao<QueueDocumentLink> {

	public QueueDocumentLinkDao() {
		super(QueueDocumentLink.class);
	}

    public List<QueueDocumentLink> getQueueDocLinks(){
    	Query query = entityManager.createQuery("SELECT q from QueueDocumentLink q");

    	@SuppressWarnings("unchecked")
        List<QueueDocumentLink> queues = query.getResultList();
        return queues;
    }

    public  List<QueueDocumentLink> getActiveQueueDocLink(){
    	Query query = entityManager.createQuery("SELECT q from QueueDocumentLink q where q.status=?1");
    	query.setParameter(1, QueueDocumentLink.ACTIVE);

    	@SuppressWarnings("unchecked")
        List<QueueDocumentLink> queues = query.getResultList();

       return queues;
    }

    public  List<QueueDocumentLink> getQueueFromDocument(Integer docId){
    	Query query = entityManager.createQuery("SELECT q from QueueDocumentLink q where q.docId=?1");
    	query.setParameter(1,docId);

    	@SuppressWarnings("unchecked")
        List<QueueDocumentLink> queues = query.getResultList();

        return queues;
    }

    public  List<QueueDocumentLink> getDocumentFromQueue(Integer qId){
    	Query query = entityManager.createQuery("SELECT q from QueueDocumentLink q where queueId=?1");
    	query.setParameter(1, qId);

    	@SuppressWarnings("unchecked")
        List<QueueDocumentLink> queues = query.getResultList();

    	return queues;
    }

    public boolean hasQueueBeenLinkedWithDocument(Integer dId,Integer qId){
    	Query query = entityManager.createQuery("SELECT q from QueueDocumentLink q where q.docId=?1 and q.queueId=?2");
    	query.setParameter(1, dId);
    	query.setParameter(2, qId);
    	@SuppressWarnings("unchecked")
        List<QueueDocumentLink> queues = query.getResultList();

        return (queues.size()>0);
    }

	/**
	 * Sets status of document to inactive "I" in queue_document_link table
	 * @param docId id of document to set to inactive
	 */
    public void setStatusInactive(Integer docId)
	{
        List<QueueDocumentLink> queuedDocuments = getQueueFromDocument(docId);

        if(!queuedDocuments.isEmpty())
		{
            QueueDocumentLink document = queuedDocuments.get(0);
            if(document.getStatus() != null && !document.getStatus().equals(QueueDocumentLink.INACTIVE))
			{
				document.setStatus(QueueDocumentLink.INACTIVE);
                merge(document);
            }
        }
    }
    public void addActiveQueueDocumentLink(Integer qId,Integer dId){
        try{
            if(!hasQueueBeenLinkedWithDocument(dId,qId)){
               QueueDocumentLink qdl = new QueueDocumentLink();
               qdl.setDocId(dId);
               qdl.setStatus(QueueDocumentLink.ACTIVE);
               qdl.setQueueId(qId);
               persist(qdl);
           }
        }catch(Exception e){
            MiscUtils.getLogger().error("Error", e);
        }
    }
    
    public void addToQueueDocumentLink(Integer qId,Integer dId){
        try{
            if(!hasQueueBeenLinkedWithDocument(dId,qId)){
               QueueDocumentLink qdl = new QueueDocumentLink();
               qdl.setDocId(dId);
               qdl.setQueueId(qId);
               persist(qdl);
           }
        }catch(Exception e){
        	MiscUtils.getLogger().error("Error", e);
        }
    }
}
