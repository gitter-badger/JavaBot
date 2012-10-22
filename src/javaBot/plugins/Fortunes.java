package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import wei2912.utilities.Generator;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;

@PluginImplementation

/** A plugin that lets you generate humor */
public class Fortunes extends javaBotPluginAbstract {
	Generator generator = new Generator();
    static ArrayList<String> fortunesArray = new ArrayList<String>();
    File                     file;
    BufferedReader           in;
    String                   line;

    public void onStart() {
        pluginHelp.addEntry("fortune", "fortune", "Generates fortunes!");
        
        try {
        	final File folder = new File("fortunes/");
        	String[] list = listFilesForFolder(folder);
        	
        	for (String filename: list) {
        		file = new File(filename);
        		
        		this.in   = new BufferedReader(new FileReader(this.file));
        		this.line = this.in.readLine();

        		StringBuffer temp = new StringBuffer("");
            
        		while (this.line != null) {
        			if (this.line.equals("%")) {
        				Fortunes.fortunesArray.add(temp.toString());
        				temp = new StringBuffer("");
        			}
        			else {
        				temp.append(this.line);
        			}
            	
        			this.line = this.in.readLine();
        		}
        	}
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        this.bot     = bot;
        this.message = message;
        this.sender  = sender;
    }

    @Override
    public void run() {
        if (matchesReference("fortune")) {
        	if (Fortunes.fortunesArray.size() == 0) {
        		bot.notice(sender, "No fortunes have been found.");
        	}
        	else {
        		final int a = generator.nextInt(0, Fortunes.fortunesArray.size() - 1);
        		bot.notice(sender, Fortunes.fortunesArray.get(a));
        	}
        }
    }
    
	public String[] listFilesForFolder(final File folder) {
		ArrayList<String> array = new ArrayList<String>();
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            array.add(fileEntry.getName());
	        }
	    }
	    
	    return array.toArray(new String[array.size()]);
	}
}


//~ Formatted by Jindent --- http://www.jindent.com
