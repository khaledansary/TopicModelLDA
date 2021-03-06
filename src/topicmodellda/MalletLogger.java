/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package topicmodellda;

/**
 *
 * @author khaledd
 */

import java.util.logging.*;
import java.io.*;

public class MalletLogger extends Logger
{

	// Initialize the java.util.logging.config properties to the MALLET default config file
	// in cc.mallet.util.resources.logging.properties

	//Create an array that allows us to reference the java logging levels by a simple integer.
	static public Level[] LoggingLevels = {Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO,
	                                       Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST,
	                                       Level.ALL};

	static {
		if (System.getProperty("java.util.logging.config.file") == null
				&& System.getProperty("java.util.logging.config.class") == null) {
			// TODO What is going on here?  This is causing an error
			//System.setProperty("java.util.logging.config.class", "cc.mallet.util.Logger.DefaultConfigurator");
			try {
				InputStream s = MalletLogger.class.getResourceAsStream ("H:\\Data\\stoplists\\logging.properties");
				if (s == null)
					throw new IOException ();
				LogManager.getLogManager().readConfiguration(s);
				Logger.global.config ("Set java.util.logging properties from "+
															MalletLogger.class.getPackage().getName() + "H:\\Data\\stoplists\\logging.properties");
			} catch (IOException e) {
				System.err.println ("Couldn't open "+MalletLogger.class.getName()+"H:\\Data\\stoplists\\logging.properties file.\n"
														+" Perhaps the 'resources' directories weren't copied into the 'class' directory.\n"
														+" Continuing.");
			}
		}
	}

	protected MalletLogger (String name, String resourceBundleName)
	{
		super (name, resourceBundleName);
	}

	public static Logger getLogger (String name)
	{
		return Logger.getLogger (name);
	}

	/** Convenience method for finding the root logger.
	*/
	public Logger getRootLogger()
	{
		Logger rootLogger = this;
		while (rootLogger.getParent() != null) {
			rootLogger = rootLogger.getParent();
		}
		return rootLogger;
	}


}
