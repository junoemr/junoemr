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
package org.oscarehr.common.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Explain;
import org.oscarehr.common.search.AbstractCriteriaSearch;
import org.oscarehr.util.MiscUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ParamAppender;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED)
public abstract class AbstractDao<T extends AbstractModel<?>> {
	public static final int MAX_LIST_RETURN_SIZE = 5000;

	protected Class<T> modelClass;

	@PersistenceContext(unitName = "persistenceUnit")
	protected EntityManager entityManager = null;

	@PersistenceContext(unitName = "readOnlyPersistenceUnit")
	protected EntityManager readOnlyEntityManager = null;

	protected AbstractDao(Class<T> modelClass) {
		setModelClass(modelClass);
	}

	/**
	 * aka update
	 */
	public AbstractModel<?> merge(AbstractModel<?> o) {
		return entityManager.merge(o);
	}

	/**
	 * aka create
	 */
	public void persist(AbstractModel<?> o) {
		entityManager.persist(o);
	}

	/**
	 * You can only remove attached instances.
	 */
	public void remove(AbstractModel<?> o) {
		entityManager.remove(o);
	}

	/**
	 * You can only refresh attached instances.
	 */
	public void refresh(AbstractModel<?> o) {
		entityManager.refresh(o);
	}

	public T find(Object id) {
		return (entityManager.find(modelClass, id));
	}

	/**
	 * Fetches all instances of the persistent class handled by this DAO. 
	 * 
	 * @return
	 * 		Returns all instances available in the backend  
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll(Integer offset, Integer limit) {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName());
		
		if (offset != null && offset > 0) {
			query.setFirstResult(offset);
		}
		// mandatory set limit
		int intLimit = (limit == null) ? getMaxSelectSize() : limit;
		if (intLimit > getMaxSelectSize()) {
			throw new MaxSelectLimitExceededException(getMaxSelectSize(), limit);
		}
		query.setMaxResults(intLimit);
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<T> criteriaSearch(AbstractCriteriaSearch criteriaSearch)
	{
		Session session = (Session) entityManager.getDelegate();

		Criteria criteria = session.createCriteria(modelClass);
		criteria = criteriaSearch.setCriteriaProperties(criteria);

		criteria.setMaxResults(criteriaSearch.getLimit());
		criteria.setFirstResult(criteriaSearch.getOffset());

		return criteria.list();
	}
	@SuppressWarnings("unchecked")
	public Integer criteriaSearchCount(AbstractCriteriaSearch criteriaSearch)
	{
		Session session = (Session) entityManager.getDelegate();

		Criteria criteria = session.createCriteria(modelClass);
		criteria = criteriaSearch.setCriteriaProperties(criteria);
		criteria.setProjection(Projections.rowCount());

		Object result = criteria.uniqueResult();
		if(result == null)
		{
			MiscUtils.getLogger().error("Criteria search count returned null result");
			result = -1;
		}
		return ((Integer)result);
	}
	
	protected int getMaxSelectSize() {
	    return MAX_LIST_RETURN_SIZE;
    }

	/** Removes an entity based on the ID
	 * 
	 * @param id
	 * 		ID of the entity to be removed
	 * @return
	 * 		Returns true if entity has been removed and false otherwise
	 */
	public boolean remove(Object id) {
		T abstractModel = find(id);
		if (abstractModel == null) {
			return false;
		}

		remove(abstractModel);
		return true;
	}

	protected T getSingleResultOrNull(Query query) {
		query.setMaxResults(1);

		@SuppressWarnings("unchecked")
		List<T> results = query.getResultList();
		if (results.size() == 1) return (results.get(0));
		else if (results.size() == 0) return (null);
		// this should never happen if we set max results to 1 :)
		else throw (new NonUniqueResultException("SingleResult requested but result was not unique : " + results.size()));
	}
	
	protected Long getCountResult(Query query) {
		query.setMaxResults(1);

		@SuppressWarnings("unchecked")
		List<Long> results = query.getResultList();
		if (results.size() == 1) return (results.get(0));
		else if (results.size() == 0) return (null);
		// this should never happen if we set max results to 1 :)
		else throw (new NonUniqueResultException("SingleResult requested but result was not unique : " + results.size()));
	}

	public int getCountAll() {
		// new JPA way of doing it, but our hibernate is too old or doesn't support primitives yet?
		// String sqlCommand="select count(*) from "+modelClass.getSimpleName();
		// Query query = entityManager.createNativeQuery(sqlCommand, Integer.class);
		// return((Integer)query.getSingleResult());

		String tableName = modelClass.getSimpleName();
		javax.persistence.Table t = modelClass.getAnnotation(javax.persistence.Table.class);
		if (t != null && t.name() != null && t.name().length() > 0) {
			tableName = t.name();
		}

		// older hibernate work around
		String sqlCommand = "select count(*) from " + tableName;
		Query query = entityManager.createNativeQuery(sqlCommand);
		return (((Number) query.getSingleResult()).intValue());
	}

	/**
	 * Gets base JPQL query for the model class.
	 * 
	 * @return
	 * 		Returns the JPQL clause in the form of <code>"FROM {@link #getModelClassName()} AS e "</code>. <code>e</code> stands for "entity"
	 */
	protected String getBaseQuery() {
		return getBaseQueryBuf(null, null).toString();
	}

	protected String getBaseQuery(String alias) {
		return getBaseQueryBuf(null, alias).toString();
	}

	/**
	 * Creates new string builder containing the base query with the specified select and alias strings
	 * 
	 * @param select
	 * 		Select clause to be appended to the query. May be null
	 * @param alias
	 * 		Alias to be used for referencing the base entity class
	 * @return
	 * 		Returns the string buffer containing the base query 
	 */
	protected StringBuilder getBaseQueryBuf(String select, String alias) {
		StringBuilder buf = new StringBuilder();
		if (select != null) {
			buf.append(select);
			buf.append(" ");
		}
		buf.append("FROM ");
		buf.append(getModelClassName());
		if (alias != null) buf.append(" AS ").append(alias).append(" ");
		return buf;
	}

	public Class<T> getModelClass() {
		return modelClass;
	}

	protected Query createQuery(String alias, String whereClause) {
		return createQuery(null, alias, whereClause);
	}

	/**
	 * Creates a query with the specified entity alias and where clause
	 * 
	 * <p/>
	 * 
	 * For example, invoking
	 * 
	 * <pre>
	 * 		createQuery("select entity.id" "entity", "entity.propertyName like :propertyValue");
	 * </pre>
	 * 
	 * would create query:
	 * 
	 * <pre>
	 * 		SELECT entity.id FROM ModelClass AS entity WHERE entity.propertyName like :propertyValue
	 * </pre>
	 * 
	 * @param select
	 * 		Select clause to be included in the query 
	 * @param alias
	 * 		Alias to be included in the query
	 * @param whereClause
	 * 		Where clause to be included in the query
	 * @return
	 * 		Returns the query
	 */
	protected Query createQuery(String select, String alias, String whereClause) {
		StringBuilder buf = createQueryString(select, alias, whereClause);
		return entityManager.createQuery(buf.toString());
	}

	/**
	 * Creates query string for the specified alias and where clause 
	 * 
	 * @param select
	 * 		Select clause
	 * @param alias
	 * 		Alias to be included in the query
	 * @param whereClause
	 * 		Where clause to be included in the query
	 * @return
	 * 		Returns the query string
	 * 
	 * @see #createQuery(String, String)
	 */
	protected StringBuilder createQueryString(String select, String alias, String whereClause) {
		StringBuilder buf = getBaseQueryBuf(select, alias);
		if (whereClause != null && !whereClause.isEmpty()) {
			buf.append("WHERE ");
			buf.append(whereClause);
		}
		return buf;
	}

	protected StringBuilder createQueryString(String alias, String whereClause) {
		return createQueryString(null, alias, whereClause);
	}

	/**
	 * Gets name of the model class.
	 * 
	 * @return
	 * 		Returns the class name without package prefix
	 */
	protected String getModelClassName() {
		return getModelClass().getSimpleName();
	}

	private void setModelClass(Class<T> modelClass) {
		this.modelClass = modelClass;
	}

	/**
	 * Saves or updates the entity based on depending if it's persistent, as determined by {@link AbstractModel#isPersistent()} 
	 * 
	 * @param entity
	 * 		Entity to be saved or updated
	 * @return
	 * 		Returns the entity
	 */
	public T saveEntity(T entity) {
		if (entity.isPersistent()) merge(entity);
		else persist(entity);
		return entity;
	}

	/**
	 * Runs native SQL query.
	 * 
	 * @param sql
	 * 		SQL query to run.
	 * @return
	 * 		Returns list containing query results.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object[]> runNativeQuery(String sql) {
		Query query = entityManager.createNativeQuery(sql);
		List resultList = query.getResultList();
		return resultList;
	}

	/**
	 * Gets parameter appender with default base query set 
	 * 
	 * @return
	 * 		Returns new appender
	 * 
	 * @see #getBaseQuery()
	 */
	protected ParamAppender getAppender() {
		return new ParamAppender(getBaseQuery());
	}

	/**
	 * Gets parameter appender with default base query set 
	 * 
	 * @param alias
	 * 		Alias to be used in the query
	 * @return
	 * 		Returns new appender
	 * 
	 * @see #getBaseQuery(String)
	 */
	protected ParamAppender getAppender(String alias) {
		return new ParamAppender(getBaseQuery(alias));
	}
	
	protected final void setDefaultLimit(Query query)
	{
		query.setMaxResults(getMaxSelectSize());
	}

	protected final void setLimit(Query query, int itemsToReturn)
	{
		if (itemsToReturn > getMaxSelectSize()) throw(new IllegalArgumentException("Requested too large of a result list size : " + itemsToReturn));

		query.setMaxResults(itemsToReturn);
	}

	protected final void setLimit(Query query, int startIndex, int itemsToReturn)
	{
		query.setFirstResult(startIndex);
		setLimit(query, itemsToReturn);
	}


	/**
	 * Run explain on a raw sql statement
	 * This will run the query string as unescaped natural sql. when using this method, ensure that all sql is safe before hand
	 * @param userQueryString raw sql string
	 * @return list of Explain results
	 */
	@Transactional(readOnly = true)
	public List<Explain> getExplainResultList(String userQueryString)
	{
		// use string concat with explain over setParameter, as the parameter gives invalid sql syntax
		Query query = readOnlyEntityManager.createNativeQuery("EXPLAIN " + userQueryString);
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.getResultList();
		return toExplainList(list);
	}
	/**
	 * Run explain on a raw sql statement
	 * This will run the query string as unescaped natural sql. when using this method, ensure that all sql is safe before hand
	 * @param jpaPreparedSQL jpa native sql string
	 * @param jpaParams jpa prepared parameter map
	 * @return list of Explain results
	 */
	@Transactional(readOnly = true)
	public List<Explain> getPreparedExplainResultList(String jpaPreparedSQL, Map<String, String[]> jpaParams)
	{
		// use string concat with explain over setParameter, as the parameter gives invalid sql syntax
		@SuppressWarnings("unchecked")
		List<Object[]> list = runPreparedNativeQuery("EXPLAIN " + jpaPreparedSQL, jpaParams);
		return toExplainList(list);
	}
	@Transactional(readOnly = true)
	public List<Explain> getIndexPreparedExplainResultList(String jpaPreparedSQL, Map<Integer, String[]> jpaParams)
	{
		// use string concat with explain over setParameter, as the parameter gives invalid sql syntax
		@SuppressWarnings("unchecked")
		List<Object[]> list = runIndexPreparedNativeQuery("EXPLAIN " + jpaPreparedSQL, jpaParams);
		return toExplainList(list);
	}

	/**
	 * convert a list of object[] to Explain results
	 * @param list
	 * @return
	 */
	private List<Explain> toExplainList(List<Object[]> list)
	{
		List<Explain> results = new ArrayList<>(list.size());

		for(Object[] result : list)
		{
			Explain explain = new Explain();
			explain.setId((BigInteger) result[0]);
			explain.setSelectType((String) result[1]);
			explain.setTable((String) result[2]);
			explain.setType((String) result[3]);
			explain.setPossibleKeys((String) result[4]);
			explain.setKey((String) result[5]);
			explain.setKeyLen((String) result[6]);
			explain.setRef((String) result[7]);
			explain.setRows((BigInteger) result[8]);
			explain.setExtra((String) result[9]);

			results.add(explain);
		}
		return results;
	}

	/**
	 * TODO - code left here for future use if spring upgraded to version 2.0 or higher.
	 * Reason: the Tuple object can be used to retrieve column names as alias.
	 * This code runs fine, but there was no way to display the column names for user queries.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public List<Object[]> runPreparedNativeQuery(String jpaPreparedSQL, Map<String, String[]> jpaParams)
	{
		Query query = readOnlyEntityManager.createNativeQuery(jpaPreparedSQL);

		for(String parameter : jpaParams.keySet())
		{
			String[] paramValue = jpaParams.get(parameter);
			if(paramValue.length == 1)
			{
				// for single parameters, use the value
				query.setParameter(parameter, paramValue[0]);
			}
			else
			{
				// otherwise use the list
				query.setParameter(parameter, paramValue);
			}
		}
		List<Object[]> results = query.getResultList();
		return results;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public List<Object[]> runIndexPreparedNativeQuery(String jpaPreparedSQL, Map<Integer, String[]> indexedParams)
	{
		Query query = readOnlyEntityManager.createNativeQuery(jpaPreparedSQL);

		for(Integer parameter : indexedParams.keySet())
		{
			String[] paramValue = indexedParams.get(parameter);
			if(paramValue.length == 1)
			{
				// for single parameters, use the value
				query.setParameter(parameter, paramValue[0]);
			}
			else
			{
				// otherwise use the list
				query.setParameter(parameter, paramValue);
			}
		}
		List<Object[]> results = query.getResultList();
		return results;
	}
}
