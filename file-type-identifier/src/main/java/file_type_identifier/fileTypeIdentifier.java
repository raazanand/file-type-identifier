package main.java.file_type_identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class fileTypeIdentifier {

    static HashMap<String, ExtensionsData> extensionsMap = new HashMap<String, ExtensionsData>();
    static HashMap<String, FileInfo> fileInfoMap = new HashMap<String, FileInfo>();
    static HashMap<String, fileProInfo> fileProInfoMap = new HashMap<String, fileProInfo>();

    public static void main(String[] args) throws IOException, org.json.simple.parser.ParseException, ParseException {

        // Input for input file .txt
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the .txt file present in /file-type-data/");
        String inputFileName = br.readLine();

        if (inputFileName.isEmpty() || !inputFileName.endsWith(".txt")) {
            System.out.println("\nDefault input file taken.");
            inputFileName = "defaultInput.txt";
        }
        System.out.println("\nInput File: " + inputFileName);



        // Reading names of the files along with Extensions from .txt file 'inputFileTypes/*.txt' to an ArrayList of String
        BufferedReader inputFileNames = new BufferedReader(new FileReader("inputFileTypes/" + inputFileName));
        List<String> inputNamesList = new ArrayList<String>();
		String fileNameAndExtension;

        while((fileNameAndExtension = inputFileNames.readLine()) != null) {
            inputNamesList.add(fileNameAndExtension);
        }
        inputFileNames.close();
        // System.out.println(inputList);



        // Reading Source1 from Extension.json and loading in into extensionMap (HashMap)
		JSONParser jsonParserS1 = new JSONParser();
		try(FileReader reader = new FileReader("data/Extensions.json"))	{
			Object obj = jsonParserS1.parse(reader);
            ArrayList<Object> fileTypeList = new ArrayList<Object>();
			fileTypeList.add(obj);

            // Iterate over fileTypesList - JSON Array to get the extensions and its details
            for(Object fileExtension : fileTypeList) {
                parseExtensions((JSONObject) fileExtension);
            }
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        // Reading Source2 from FileInfo.json and loading in into FileInfoMap (HashMap)
		JSONParser jsonParserS2 = new JSONParser();
		try(FileReader reader = new FileReader("data/FileInfo.json"))	{
			Object obj = jsonParserS2.parse(reader);
			JSONArray fileTypeList = (JSONArray) obj;

            // Iterate over fileTypesList - JSON Array to get the extensions and its details
            for(Object fileExtension : fileTypeList) {
                parseFileInfo((JSONObject) fileExtension);
            }
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        // Reading Source3 from fileProInfo.json and loading in into fileProInfoMap (HashMap)
		JSONParser jsonParserS3 = new JSONParser();
		try(FileReader reader = new FileReader("data/fileProInfo.json"))	{
			Object obj = jsonParserS3.parse(reader);
			JSONArray fileTypeList = (JSONArray) obj;

            // Iterate over fileTypesList - JSON Array to get the extensions and its details
            for(Object fileExtension : fileTypeList) {
                parsefileProInfo((JSONObject) fileExtension);
            }
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}



        // Creating a File object that represents the disk file for writing output on a output.txt file
        PrintStream o = new PrintStream(new File("output.txt"));

        // Store current System.out before assigning a new value
        PrintStream console = System.out;

        // Assign o to output stream
        System.setOut(o);

        for(String input: inputNamesList) {
            // Extracting file Extension from each input
            int dotLastIndex = input.lastIndexOf(".");
            String fileExtension = input.substring(dotLastIndex).toLowerCase();

            // Setting the default values of the properties of input file
            String type = "N/A";
            String category = "N/A";
            String description = "N/A";
			String programs = "N/A";

            if(fileInfoMap.containsKey(fileExtension)) // Searching in FileInfo HashMap
            {
                FileInfo fileInfoObject = fileInfoMap.get(fileExtension);
                type = fileInfoObject.fileInfoType;
				category = fileInfoObject.fileInfoCategory;
				description = fileInfoObject.fileInfoDescription;
				programs = fileInfoObject.fileInfoPrograms;
            }
            else if (fileProInfoMap.containsKey(fileExtension))	// Searching in fileProInfoMap HashMap
			{
				fileProInfo fileProInfoObject = fileProInfoMap.get(fileExtension);
				category = fileProInfoObject.fileProInfoComment;
				type = fileProInfoObject.fileProInfoType;
			}
            else if(extensionsMap.containsKey(fileExtension)) // Searching in Extension HashMap
            {
                ExtensionsData extensionsObject = extensionsMap.get(fileExtension);
                type = extensionsObject.extensionsType;
                description = extensionsObject.extensionsDescription;
            }



            // Outputting the Categories
            System.out.println(
					"\nFile\t: " + input +
					"\n " +
					"\nCategory\t: " + category +
                    "\n " +
					"\nType\t\t: " + type +
                    "\n " +
					"\nDescription\t: " + description +
                    "\n " +
					"\nPrograms\t: " + programs +
                    "\n " +
                    "\n============================================================================================================================================================================================================\n ");
        }

        // Use stored value for output stream
        System.setOut(console);
        System.out.println("\nSuccess\n");
    }



    // Methods for parsing .json file
    private static void parseExtensions(JSONObject file) {
        // Iterating over each file Extension in the JSON Object of Extension.JSON file
        Iterator<?> itr = file.keySet().iterator();
        while(itr.hasNext()) {
            String key = (String) itr.next();	// Get the key - fileExtension
		    JSONObject extensionObject = (JSONObject) file.get(key);

		    // Getting values for each fileExtension
            String type = (String) extensionObject.get("type");
            String description = (String) extensionObject.get("description");

		    // Updating HashMap - extensionMap for each key (extension)
		    ExtensionsData extensionsObj = new ExtensionsData();
			extensionsObj.extensionsType = type;
			extensionsObj.extensionsDescription = description;
			extensionsMap.put(key.toLowerCase(), extensionsObj);
        }
    }

    private static void parseFileInfo(JSONObject file) {
        //Getting each values for the respective keys from JSON Object
        String extension = (String) file.get("Extension");
        String category = (String) file.get("Category");
        String type = (String) file.get("Type");
        String description = (String) file.get("Description");
        String programs = (String) file.get("Programs");

        //Initializing the FileInfo class variables and Updating FileInfo HashMap
        FileInfo fileInfoObject = new FileInfo();
        fileInfoObject.fileInfoType = type;
        fileInfoObject.fileInfoCategory = category;
        fileInfoObject.fileInfoDescription = description;
        fileInfoObject.fileInfoPrograms = programs;

        fileInfoMap.put(extension.toLowerCase(),fileInfoObject);
    }

    private static void parsefileProInfo(JSONObject file) {
        // Iterating over each file Extension in the JSON Object of fileProInfo.JSON file
        Iterator<?> itr = file.keySet().iterator();
        while(itr.hasNext()) {
            String key = (String) itr.next();	// Get the key - fileExtension
		    JSONObject extensionObject = (JSONObject) file.get(key);

		    // Getting values for each fileExtension
		    String type = (String) extensionObject.get("fileProInfoType");
		    String comment = (String) extensionObject.get("fileProInfoComment");

		    // Updating Hash map - fileProInfoHM for each key (extension)
		    fileProInfo fileProInfoObject = new fileProInfo();
			fileProInfoObject.fileProInfoComment = comment;
			fileProInfoObject.fileProInfoType = type;
			fileProInfoMap.put(key.toLowerCase(), fileProInfoObject);
        }
	}
}
