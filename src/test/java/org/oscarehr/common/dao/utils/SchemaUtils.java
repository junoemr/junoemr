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
// -----------------------------------------------------------------------------------------------------------------------
// *
// *
// * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved. *
// * This software is published under the GPL GNU General Public License.
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version. *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details. * * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. *
// *
// * <OSCAR TEAM>
// * This software was written for the
// * Department of Family Medicine
// * McMaster University
// * Hamilton
// * Ontario, Canada
// *
// -----------------------------------------------------------------------------------------------------------------------

package org.oscarehr.common.dao.utils;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This class is a utility class to create and drop the database schema and all it's bits.
 * This class is highly database specific and alternate versions of this class will have to be
 * written for other databases.
 */
public class SchemaUtils
{
	private static Logger logger=MiscUtils.getLogger();
	
	public static boolean inited=false;
	public static Map<String,String> createTableStatements = new HashMap<String,String>();

	private static Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		String driver=ConfigUtils.getProperty("db_driver");
		String user=ConfigUtils.getProperty("db_user");
		String password=ConfigUtils.getProperty("db_password");
		String urlPrefix=ConfigUtils.getProperty("db_url_prefix");

		Class.forName(driver).newInstance();

		return(DriverManager.getConnection(urlPrefix, user, password));
	}

	/**
	 * Intent is to support junit tests.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void dropDatabaseIfExists() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Connection c=getConnection();
		try
		{
			Statement s=c.createStatement();
			s.executeUpdate("drop database if exists "+ConfigUtils.getProperty("db_schema"));
		}
		finally
		{
			c.close();
		}
	}

	/**
	 * Intent is for new installations.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static void createDatabaseAndTables() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		boolean skipDbInit = false;
		if(System.getProperty("oscar.skip.dbinit") != null && System.getProperty("oscar.skip.dbinit").equalsIgnoreCase("true"))
			skipDbInit=true;
		
		String schema=ConfigUtils.getProperty("db_schema");
		logger.info("using schema : "+schema);
		
		Connection c=getConnection();
		try
		{
			Statement s=c.createStatement();
			
			if(!skipDbInit) {
				s.executeUpdate("create database "+schema);				
			}
			s.executeUpdate("use "+schema);

			runCreateTablesScript(c);
		}
		finally
		{
			c.close();
		}
	}


	public static void restoreTable(String... tableNames) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		restoreTable(true,tableNames);
	}
	
	private static boolean isWindows() {
		String osName = System.getProperty("os.name");
		return osName.toLowerCase().contains("windows");
	}

	public static Set<String> getFailedChecksums()
		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Connection connection = getConnection();
		Statement statement = connection.createStatement();

		Set<String> errors = new HashSet<String>();
		try
		{
			String schema = ConfigUtils.getProperty("db_schema");
			statement.executeUpdate("use " + schema);

			for (String tableName : createTableStatements.keySet())
			{
				// Check if there are any modified tables
				String checksumValueSql = "CHECKSUM TABLE `" + tableName + "`";
				ResultSet checksumResult = statement.executeQuery(checksumValueSql);

				String defaultChecksum = null;
				String checksum = null;
				if (checksumResult.next())
				{
					checksum = checksumResult.getString("Checksum");
				}

				String checksumValueDefaultSql = "CHECKSUM TABLE `" + tableName + "_maventest`";
				ResultSet checksumDefaultResult = statement.executeQuery(checksumValueDefaultSql);

				if (checksumDefaultResult.next())
				{
					defaultChecksum = checksumDefaultResult.getString("Checksum");
				}

				if (checksum != null && !checksum.equals(defaultChecksum))
				{
					String errorMessage = "\n***** Checksums don't match for table \"" + tableName + "\"\n" +
						"***** This means a test modified the database and didn't clean up after itself.\n" +
						"***** It's possible that it wasn't this test's fault, but one of the previous tests.\n";

					logger.error(errorMessage);
					errors.add(errorMessage);
				}
			}
		}
		finally
		{
			statement.close();
			connection.close();
		}

		return errors;
	}

	public static void restoreTable(boolean includeInitData, String... tableNames) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		long start = System.currentTimeMillis();
		String schema = ConfigUtils.getProperty("db_schema");

		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		try
		{
			statement.executeUpdate("use "+schema);
			statement.executeUpdate("set foreign_key_checks = 0");
			for (int i = 0; i < tableNames.length; i++)
			{
				String tableName = tableNames[i];
				if (isWindows())
				{
					tableName = tableName.toLowerCase(); // make it case insensitive by default
				}
				String sql = "truncate table " + tableName;
				statement.executeUpdate(sql);
				if (includeInitData)
				{
					statement.executeUpdate("insert into " + tableName + " select * from " + tableName +
						"_maventest");
				}
            }
		}
		finally
		{
			statement.executeUpdate("set foreign_key_checks = 1");
			statement.close();
			connection.close();
		}
		long end = System.currentTimeMillis();
		long secsTaken = (end-start)/1000;
		if(secsTaken > 30) {
			MiscUtils.getLogger().info("Restoring db took " + secsTaken + " seconds.");
		}
	}

	
	public static void restoreAllTables()
		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		long start = System.currentTimeMillis();
		String schema=ConfigUtils.getProperty("db_schema");

		Connection c=getConnection();
		Statement s=c.createStatement();
		try
		{
			s.executeUpdate("use "+schema);
			s.executeUpdate("SET foreign_key_checks = 0");
			for (String tableName:createTableStatements.keySet()) {
				if("dashboard_report_view".equals(tableName))
				{
					continue;
				}
				try
				{
					s.executeUpdate("truncate table " + tableName);
					s.executeUpdate("insert into " + tableName + " select * from " + tableName + "_maventest");
				}
				catch(SQLException e)
				{
					logger.error("failed to restore table [" + tableName +"] with error: " + e.getMessage());
					throw e;
				}
            }

		}
		finally
		{
			s.executeUpdate("SET foreign_key_checks = 1");
			s.close();
			c.close();
		}
		long end = System.currentTimeMillis();
		long secsTaken = (end-start)/1000;
		if(secsTaken > 30) {
			MiscUtils.getLogger().info("Restoring db took " + secsTaken + " seconds.");
		}
	}

	public static int loadFileIntoMySQL(String filename) throws IOException
	{
		String dir = new File(filename).getParent();
                String env[] = null;
                String envStr = null;
                if (System.getProperty("os.name").startsWith("Windows")){
                    envStr = "SYSTEMROOT="+System.getenv("SYSTEMROOT");
                }
                if (envStr != null) {
                    env = new String[]{envStr};
                } else {
                    env = new String[]{};
                }

        String[] commandString={
            	"mysql",
				"--user="+ConfigUtils.getProperty("db_user"),
				"--password="+ConfigUtils.getProperty("db_password"),
				"--host="+ConfigUtils.getProperty("db_host"),
				"-e",
				"source "+filename,
				ConfigUtils.getProperty("db_schema")
                };
        logger.info("Runtime exec command string : "+Arrays.toString(commandString));
		Process p = Runtime.getRuntime().exec(commandString,env, new File(dir));
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // read the output from the command
        String s= null;
        while ((s = stdInput.readLine()) != null) {
            MiscUtils.getLogger().info(s);
        }

        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
        	 MiscUtils.getLogger().info(s);
        }

        int exitValue = -1;
        try {
        	exitValue = p.waitFor();
        }catch(InterruptedException e) {
        	throw new IOException("error with process");
        }

        stdInput.close();
        stdError.close();

		return exitValue;
	}

	/**
	 * Replace constraint names with name + "_original".
	 * @param sql The input sql to modify
	 * @return The updated-constraint sql
	 */
	private static String renameConstraint(String sql)
	{
		if(sql == null) {
			return null;
		}

	    return sql.replaceAll("CONSTRAINT `(.*?)`", String.format("CONSTRAINT `$1%s`", "_original"));
	}

	/**
	 * When running test in Intellij, "basedir" is not available and returns
	 * null.
	 *
	 * @return
	 */
	protected static String getBaseDir()
	{
		logger.debug("Get \"basedir\" property (must be set by Maven)");
		String basedir = System.getProperty("basedir");

		if (basedir == null)
		{
			logger.info("Missing \"basedir\" to run tests (normally set by Maven). Assuming Intellij test run, look for env. variable");
			basedir = System.getenv().get("basedir");

			if (basedir == null)
			{
				logger.warn("Missing \"basedir\" to run tests (normally set by Maven). Fallback to \"user.dir\" but if tests fails " +
						"set env. variable \"basedir=$MODULE_DIR$\" in run configuration");
				basedir = System.getProperty("user.dir");
			}
		}

		return basedir;
	}

	private static void runCreateTablesScript(Connection c) throws IOException
	{
		boolean skipDbInit = false;
		if(
				System.getProperty("oscar.skip.dbinit") != null &&
				System.getProperty("oscar.skip.dbinit").equalsIgnoreCase("true"))
		{
			skipDbInit = true;
		}
		
		if(!skipDbInit)
		{
			String baseDir = getBaseDir();
			logger.info("using baseDir : "+baseDir);
					
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/oscarinit.sql"),0);
	
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/oscarinit_on.sql"),0);
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/oscardata.sql"),0);
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/oscardata_on.sql"),0);
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/icd9.sql"),0);

			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/caisi/initcaisi.sql"),0);

			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/caisi/initcaisidata.sql"),0);
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/caisi/populate_issue_icd9.sql"),0);
			//assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/icd9_issue_groups.sql"),0);
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/measurementMapData.sql"),0);
			//assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/expire_oscardoc.sql"),0);

			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/oscarinit_bc.sql"),0);
			assertEquals(loadFileIntoMySQL(baseDir + "/database/mysql/oscardata_bc.sql"),0);

			// Run OscarHost updates
			File regularUpdates = new File(baseDir, "/database/mysql/oscarhost_updates_applied/");
			File[] updatesToRun = regularUpdates.listFiles();
			Arrays.sort(updatesToRun);
			for (File update : updatesToRun)
			{
				if(FilenameUtils.getExtension(update.getAbsolutePath()).equals("sql"))
				{
					assertEquals(loadFileIntoMySQL(update.getAbsolutePath()), 0);
				}
			}

			File folder = new File(baseDir, "/database/mysql/oscarhost_updates/");
			File[] updateFiles = folder.listFiles();
			Arrays.sort(updateFiles);

			for(File update_file: updateFiles) {

				if(FilenameUtils.getExtension(update_file.getAbsolutePath()).equals("sql")) {
					assertEquals(loadFileIntoMySQL(update_file.getAbsolutePath()),0);
				}
			}

			// Fix test specific problems with the database
			assertEquals(loadFileIntoMySQL(baseDir + "/src/test/java/integration/tests/sql/initialize_database.sql"), 0);
			createTableStatements.clear();
			try {
				ResultSet rs = c.getMetaData().getTables(ConfigUtils.getProperty("db_schema"), null, "%", null);
				while(rs.next()) {
					String tableName = rs.getString("TABLE_NAME");	
					
					Statement stmt2 = c.createStatement();
					ResultSet rs2 = stmt2.executeQuery("show create table " + tableName + ";");
					if(rs2.next()) {
						String sql = rs2.getString(2);
						sql = renameConstraint(sql);
						createTableStatements.put(tableName, sql);

						// Create table restore point
						Statement getTableInfoStmt = c.createStatement();
						ResultSet tableInfo = getTableInfoStmt.executeQuery("SHOW TABLE STATUS WHERE Name='" + tableName + "'");
						if (tableInfo.next())
						{
							Statement stmt = c.createStatement();
							String tableEngine = "";
							if (tableInfo.getString(2) != null)
							{
								tableEngine = " ENGINE = " + tableInfo.getString(2);
							}

							stmt.executeUpdate("CREATE TABLE " + tableName + "_maventest" + tableEngine + " SELECT * FROM " + tableName);
							//stmt.executeUpdate("alter table "+ tableName + " rename to " + tableName + "_maventest");

							stmt.close();
						}
						getTableInfoStmt.close();
					}
					rs2.close();
					stmt2.close();
				}
				rs.close();
	
	
			}catch(SQLException e) {
				MiscUtils.getLogger().error("Error:",e);
			}
		} else {
			//we're assuming a db ready to go..we just need to build the createTableStatementsMap
			createTableStatements.clear();
			try {
				ResultSet rs = c.getMetaData().getTables(ConfigUtils.getProperty("db_schema"), null, "%", null);
				while(rs.next()) {
					String tableName = rs.getString("TABLE_NAME");		
					if(!tableName.endsWith("_maventest"))
						continue;
					Statement stmt2 = c.createStatement();
					ResultSet rs2 = stmt2.executeQuery("show create table " + tableName + ";");
					if(rs2.next()) {
						String sql = rs2.getString(2).replaceAll(tableName, tableName.substring(0,tableName.length()-10));
						sql = renameConstraint(sql);
						createTableStatements.put(tableName.substring(0,tableName.length()-10), sql);
					}
					rs2.close();
					stmt2.close();
				}
				rs.close();
	
	
			}catch(SQLException e) {
				MiscUtils.getLogger().error("Error:",e);
			}
		}
				
		inited=true;
	}

	/**
	 * Intended for existing installations.
	 */
	public static void upgradeDatabase()
	{
		throw(new NotImplementedException("Not implemented yet..."));
	}

	/**
	 * Intent is for junit tests.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 */
	public static void dropAndRecreateDatabase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		boolean skipDbInit = false;
		if(System.getProperty("oscar.skip.dbinit") != null && System.getProperty("oscar.skip.dbinit").equalsIgnoreCase("true"))
			skipDbInit=true;
		if(!skipDbInit)
			dropDatabaseIfExists();
		createDatabaseAndTables();
	}

	public static void main(String... argv) throws Exception
	{
		dropAndRecreateDatabase();
	}
}
