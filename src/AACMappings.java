import java.util.NoSuchElementException;

import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner; // Import the Scanner class to read text files

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & YOUR NAME HERE
 *
 */
public class AACMappings implements AACPage {
	

	/*
	 * Fields
	 */

	 /**
	  * current working category
	  */
	AACCategory current;

	/**
	 * home category to keep track of it
	 */
	AACCategory home;
	/**
	 * associate array that keeps track of all categories and image locations associate with them
	 */
	AssociativeArray<String, AACCategory> maps;

	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 * @throws FileNotFoundException 
	 */
	public AACMappings(String filename) throws FileNotFoundException{
		this.maps = new AssociativeArray<String, AACCategory>();
		File file = new File(filename);
		Scanner scan = new Scanner(file);

		while (scan.hasNextLine()) {
			String currLine = scan.nextLine();
			String[] splitUp = currLine.split("\\s");
			String categ = splitUp[0];



			if (categ.length() <= 0) {
				System.err.println("File Is Empty");
				scan.close();
				return;
			}

			if (categ.charAt(0) == ('>')) {
				String unArrow = splitUp[0].replace(">", "");
				this.current.addItem(unArrow, splitUp[1]);

			} else {
				if (this.maps.hasKey(categ)) {
					try {
						this.current = this.maps.get(categ);
					} catch (KeyNotFoundException e) {

					}


				} else {
					AACCategory newCat = new AACCategory(splitUp[1]);
					try {
						this.maps.set(categ, newCat);
					} catch (NullKeyException e) {

					}
					this.current = newCat;
				}
			}
		}
		scan.close();
	}
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) throws NoSuchElementException{
		if (this.maps.hasKey(imageLoc)) {
			try {
				this.current = this.maps.get(imageLoc);
				return "";
			} catch (KeyNotFoundException e) {
				return "";
				// should not throw exception
			}
		} else if (this.current.hasImage(imageLoc)) {
			return this.current.select(imageLoc);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		String[] str = new String[0];
		if (this.current.getImageLocs().length == 0) {
			return str;
		} else {
			return this.current.getImageLocs();
		}
	}
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		this.current = this.home;
		return;
	}
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 * @throws IOException 
	 */
	public void writeToFile(String filename) throws IOException {
		this.maps = new AssociativeArray<String, AACCategory>();
		FileWriter wrtr = new FileWriter(filename);
		String[] cat = this.current.getImageLocs();
		for (int i = 0; i < cat.length; i++) {
			wrtr.write(cat[i]);
		}
		wrtr.close();
	}
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		this.current.addItem(imageLoc, text);
	}


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		return this.current.name;
	}


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return this.current.hasImage(imageLoc);
	}
}
