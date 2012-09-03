package javaBot.tools;

// ~--- non-JDK imports --------------------------------------------------------

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.hsqldb.jdbcDriver;

public class DatabaseReader {
	private Statement	 com	= null;
	private Connection	 con	= null;	        // Database objects
	Enumeration	         en	    = null;	        // For the entries in the
	                                                // zip file
	ZipEntry	         ent	= null;
	File	             f	    = null;	        // Used to get a temporary
	                                                // file name, not actually
	                                                // used for anything
	ZipFile	             file	= null;	        // For handeling zip files
	InputStream	         in	    = null;	        // for reading buffers from
	                                                // the zip file
	jdbcDriver	         j	    = new jdbcDriver();    // Instantiate the
	                                                // jdbcDriver from HSQL
	BufferedOutputStream	out	= null;	        // For the output from the
	                                                // zip class
	private ResultSet	 rec	= null;
	List	             v	    = new ArrayList();	// Stores list of unzipped
	                                                // file for deletion at end
	                                                // of program
	int	                 len;	                    // General length counter
	                                                // for loops
	String	             name;

	public DatabaseReader(String name) {
		this.name = name;

		// Unzip zip file, via info from
		// http://www.devx.com/getHelpOn/10MinuteSolution/20447
		try {

			// Open the zip file that holds the OO.Org Base file
			this.file = new ZipFile(System.getProperty("user.dir")
			        + "/database/" + name + ".odb");

			// Create a generic temp file. I only need to get the filename from
			// the tempfile to prefix the extracted files for OO Base
			this.f = File.createTempFile(name, "tmp");
			this.f.deleteOnExit();

			// Get file entries from the zipfile and loop through all of them
			this.en = this.file.entries();

			while (this.en.hasMoreElements()) {

				// Get the current element
				this.ent = (ZipEntry) this.en.nextElement();

				// If the file is in the database directory, extract it to our
				// temp folder using the temp filename above as a prefix
				if (this.ent.getName().startsWith("database/")) {
					final byte[] buffer = new byte[1024];

					// Create an input stream file the file entry
					this.in = this.file.getInputStream(this.ent);

					// Create a output stream to write out the entry to, using
					// the
					// temp filename created above
					this.out = new BufferedOutputStream(new FileOutputStream(
					        "/tmp/" + this.f.getName() + "."
					                + this.ent.getName().substring(9)));

					// Add the newly created temp file to the tempfile vector
					// for deleting
					// later on
					this.v.add("/tmp/" + this.f.getName() + "."
					        + this.ent.getName().substring(9));

					// Read the input file into the buffer, then write out to
					// the output file
					while ((this.len = this.in.read(buffer)) >= 0) {
						this.out.write(buffer, 0, this.len);
					}

					// close both the input stream and the output stream
					this.out.close();
					this.in.close();
				}
			}

			// Close the zip file since the temp files have been created
			this.file.close();

			// Obtain connection
			this.setCon(DriverManager.getConnection("jdbc:hsqldb:file:/tmp/"
			        + this.f.getName(), "SA", ""));
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void saveFile() {
		try {
			this.getRec().close();
			this.getCom().close();
			this.getCon().close();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		// Delete the temporary files, which file names are stored in the v
		// vector
		for (this.len = 0; this.len < this.v.size(); this.len++) {
			(new File((String) this.v.get(this.len))).delete();
		}
	}

	public Statement getCom() {
		return this.com;
	}

	public void setCom(Statement com) {
		this.com = com;
	}

	public Connection getCon() {
		return this.con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public ResultSet getRec() {
		return this.rec;
	}

	public void setRec(ResultSet rec) {
		this.rec = rec;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
