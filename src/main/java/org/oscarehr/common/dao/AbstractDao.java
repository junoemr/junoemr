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
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional(propagation = Propagation.REQUIRED)
public abstract class AbstractDao<T extends AbstractModel<?>> {
	public static final int MAX_LIST_RETURN_SIZE = 5000;

	public static final String SORT_ASC = "ASC";
	public static final String SORT_DESC = "DESC";

	protected Class<T> modelClass;

	@PersistenceContext
	protected EntityManager entityManager = null;

	@PersistenceContext
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

	public T find(Object id)
	{
		return (entityManager.find(modelClass, id));
	}

	public Optional<T> findOptional(Object id)
	{
		return Optional.ofNullable(entityManager.find(modelClass, id));
	}

	/**
	 * find entity by primary key or throw exception if not found.
	 * @param id - primary key to search by
	 * @return - the found entity
	 * @throws EntityNotFoundException - if the primary key does not match any entity
	 */
	public T findOrThrow(Object id)
	{
		T entity = (entityManager.find(modelClass, id));
		if (entity == null)
		{
			throw new EntityNotFoundException();
		}
		return entity;
	}

	/**
	 * fetch all instances of the persistent class handled by this DAO
	 * @return - all records
	 */
	public List<T> findAll()
	{
		Query query = entityManager.createQuery("FROM " + getModelClassTableName());
		return query.getResultList();
	}

	/**
	 * Fetches all instances of the persistent class handled by this DAO. 
	 * 
	 * @return
	 * 		Returns all instances available in the backend  
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll(Integer offset, Integer limit) {
		Query query = entityManager.createQuery("FROM " + getModelClassTableName());
		
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

		if(!criteriaSearch.hasNoLimit())
		{
			criteria.setMaxResults(criteriaSearch.getLimit());
		}
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
		return ((Long)result).intValue();
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

		String tableName = getModelClassTableName();
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

	protected String getModelClassTableName()
	{
		return modelClass.getSimpleName();
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
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Transactional(readOnly = true)
	public List<Object[]> runNativeQuery(String sql)
	{
		Query query = readOnlyEntityManager.createNativeQuery(escapeJpaParamCharacters(sql));
		return query.getResultList();
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
		Query query = readOnlyEntityManager.createNativeQuery("EXPLAIN " + escapeJpaParamCharacters(userQueryString));
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.getResultList();
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
		Pattern numberPattern = Pattern.compile("\\d+");

		for(Object[] result : list)
		{
			Explain explain = new Explain();
			explain.setId((BigInteger) result[0]);
			explain.setSelectType((String) result[1]);
			explain.setTable((String) result[2]);
			explain.setType((String) result[3]);
			explain.setPossibleKeys((String) result[4]);
			explain.setKey((String) result[5]);
			if (result[6] != null)
			{
				Matcher match = numberPattern.matcher((String) result[6]);
				if (match.matches())
				{
					explain.setKeyLen(match.group(0));
				}
			}
			explain.setRef((String) result[7]);

			// MariaDB 10.1 and 10.4 return the rows column of the explain result as different
			// data types.  This detects the type and sets the value appropriately.
			Object rows = result[8];
			if (rows != null)
			{
				if (rows.getClass().equals(String.class))
				{
					Matcher match = numberPattern.matcher((String) rows);
					if (match.matches())
					{
						explain.setRows(new BigInteger(match.group(0)));
					}
				}
				else if (rows.getClass().equals(BigInteger.class))
				{
					explain.setRows((BigInteger) rows);
				}
			}

			explain.setExtra((String) result[9]);

			results.add(explain);
		}
		return results;
	}

	/**
	 * escapes some special characters that the entity manager will not allow in native queries correctly.
	 */
	private String escapeJpaParamCharacters(String unescapedSql)
	{
		String escapedSql = unescapedSql
				.replaceAll(":=", "\\\\:=")
				.replaceAll("\\?", "\\\\?");
		MiscUtils.getLogger().info("ESCAPED:\n" + escapedSql);
		return escapedSql;
	}
}
