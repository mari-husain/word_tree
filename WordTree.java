import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An AVL tree specifically designed to store words from a text file along with a list of the
 * lines on which those words appear in the text file.
 * 
 * @author Maryam Husain
 * @version October 31st, 2016
 */
public class WordTree {
private AvlNode root;
	
	/**
     * Construct an empty tree.
     */
    public WordTree() {
        root = null;
    }
    
    /**
     * Construct a tree from a text file.
     * @param filename - the name of the source file
     */
    public WordTree(String filename) {
    	root = null;
    	int lineCount = 1; //set line count to 1, since we're starting on the first line
    	
    	//read in the file
    	System.out.println("Constructing tree from file: " + filename);
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(filename));
    		String line = reader.readLine();

    		//catalogue the words in the file line by line until there's nothing left
    		while (line != null) {
    			parseLine(line, lineCount);
    			line = reader.readLine();
    			lineCount++;
    		}

    		//close the reader
    		reader.close();
    	} catch (IOException e) {
    		System.out.println(e);
    	}
    	System.out.println("File successfully read.");
    	System.out.println();
    }
    
    /**
     * Parse a line into individual words and catalogue those words by line number.
     * @param line - the line to parse
     * @param lineNumber - the line number we're currently on
     */
    private void parseLine(String line, int lineNumber) {
		String[] words = line.split(" "); // split the words in a line at spaces
		for(String word:words) {
			// add each word to the AVL tree
			word = word.toLowerCase();
			word = cleanWord(word);
			if(word.length() > 0) {
				indexWord(word, lineNumber);
			}
		}
	}
    
    /**
     * Replace all punctuation characters with an empty string as per assignment
     * instructions.
     * @param word - the word to clean
     * @return the cleaned word
     */
    private String cleanWord(String word) {
    	word = word.trim();
    	word = word.replaceAll("[!\"#$%&()*+,-./:;<=>?@\\^_`{|}~]", "");
    	word = word.replaceAll("\\[", "");
    	word = word.replaceAll("\\]", "");
    	return word;
    }
    
    /**
     * Index a word with a given line number into the tree.
     * @param word - the word to index
     * @param lineNumber - the line number we're currently on
     */
    public void indexWord(String word, int lineNumber) {
    	root = indexWord(word, lineNumber, root);
    }
    
    /**
     * Helper method for the publicly-accessible indexWord method above. Indexes a word with a given
     * line number into the tree.
     * @param word -  the word to index
     * @param lineNumber - the line number we're currently on
     * @param t - the root of the subtree to index the word into
     * @return the root of the new balanced (sub)tree.
     */
    private AvlNode indexWord(String word, int lineNumber, AvlNode t)
    {
        if(t == null) { //if we can't find the node in the current tree, add it
            AvlNode newNode = new AvlNode(word, lineNumber, null, null );
            newNode.addIndex(lineNumber);
            return newNode;
        }
        
        int compareResult = word.compareTo(t.word);
        
        if(compareResult < 0) // if we haven't found it, keep searching 
            t.left = indexWord(word, lineNumber, t.left);
        else if(compareResult > 0) // if we haven't found it, keep searching
            t.right = indexWord(word, lineNumber, t.right);
        else { // if it's a duplicate, add the line number to the list
        	if(!t.indices.contains(lineNumber)) // so long as the index isn't already in there
        		t.addIndex(lineNumber);;  
        }
        return balance(t);
    }
    
    /**
     * Get all of the lines a word appears on.
     * @param word - the word you want to search for
     * @return the lines the word appears on
     */
    public List<Integer> getLinesForWord(String word) {
    	return getLinesForWord(word, root);
    }
    
    /**
     * Internal method to find an item in a subtree.
     * @param x is item to search for.
     * @param t the node that roots the tree.
     * @return true if x is found in subtree.
     */
    private List<Integer> getLinesForWord(String word, AvlNode t)
    {
        while(t != null)
        {
            int compareResult = word.compareTo(t.word);
            
            if( compareResult < 0 )
                t = t.left;
            else if( compareResult > 0 )
                t = t.right;
            else
                return t.indices;    // Match
        }

        ArrayList<Integer> emptyList = new ArrayList<Integer>();   // No match
        emptyList.add(-1);
        return emptyList;
    }
    
    /**
     * Print all words and the lines they appear on.
     */
    public void printIndex() {
    	printIndex(root);
    }
    
    /**
     * Internal method to print all words in a subtree and the lines they appear on.
     * @param t the node that roots the subtree.
     */
    private void printIndex(AvlNode t)
    {
        if(t != null) {
            printIndex(t.left);
            System.out.println(t.word + ": " + t.indices);
            printIndex(t.right);
        }
    }
    
    private static final int ALLOWED_IMBALANCE = 1;
    
    // Assume t is either balanced or within one of being balanced
    private AvlNode balance(AvlNode t)
    {
        if(t == null)
            return t;
        
        if( height( t.left ) - height( t.right ) > ALLOWED_IMBALANCE )
            if( height( t.left.left ) >= height( t.left.right ) )
                t = rotateWithLeftChild( t );
            else
                t = doubleWithLeftChild( t );
        else
        if( height( t.right ) - height( t.left ) > ALLOWED_IMBALANCE )
            if( height( t.right.right ) >= height( t.right.left ) )
                t = rotateWithRightChild( t );
            else
                t = doubleWithRightChild( t );

        t.height = Math.max( height( t.left ), height( t.right ) ) + 1;
        return t;
    }
    
    /**
     * Return the height of node t, or -1, if null.
     */
    private int height(AvlNode t) {
        return t == null ? -1 : t.height;
    }
    
    /**
     * Rotate binary tree node with left child.
     * For AVL trees, this is a single rotation for case 1.
     * Update heights, then return new root.
     */
    private AvlNode rotateWithLeftChild(AvlNode k2)
    {
        AvlNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }

    /**
     * Rotate binary tree node with right child.
     * For AVL trees, this is a single rotation for case 4.
     * Update heights, then return new root.
     */
    private AvlNode rotateWithRightChild(AvlNode k1)
    {
        AvlNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
        k2.height = Math.max(height(k2.right), k1.height) + 1;
        return k2;
    }

    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child.
     * For AVL trees, this is a double rotation for case 2.
     * Update heights, then return new root.
     */
    private AvlNode doubleWithLeftChild(AvlNode k3)
    {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child.
     * For AVL trees, this is a double rotation for case 3.
     * Update heights, then return new root.
     */
    private AvlNode doubleWithRightChild(AvlNode k1)
    {
        k1.right = rotateWithLeftChild(k1.right);
        return rotateWithRightChild(k1);
    }
	
    /**
     * Internal AVLNode class.
     * @author Maryam Husain
     * @version 10/31/2016
     */
	private static class AvlNode {
		String word;
		ArrayList<Integer> indices;
		AvlNode left;
		AvlNode right;
		int height;
		
	    // Constructors
        AvlNode(String word, int lineNumber)
        {
            this(word, lineNumber, null, null );
        }

        AvlNode(String word, int lineNumber, AvlNode lt, AvlNode rt)
        {
            this.word  = word;
            indices = new ArrayList<Integer>();
            left     = lt;
            right    = rt;
            height   = 0;
        }
        
        void addIndex(int index) {
        	indices.add(index);
        }
	}
}
