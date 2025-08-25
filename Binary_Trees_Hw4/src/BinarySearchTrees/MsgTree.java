package BinarySearchTrees;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.ArrayDeque;


/**
 * @author Christian Salazar
 * Build a data tree structure using nodes to decode encrypted data with binary values associated with ASCII characters.
 */
public class MsgTree {

	public MsgTree left;
	public MsgTree right;
	public char payloadChar;
	
	// Optional but we're using it to help
	private static int staticCharIdx = 0;
	
	private static boolean constructing = false;
	/**
	 * Build a binary tree with recursion
	 * 
	 * @param encodingString
	 */
	public MsgTree(String encodingString) {

		// So we don't skip beginning root when building our tree
		boolean isRoot = !constructing;

		if (isRoot) 
		{
			// RESET THESE THINGS or else you'll be in a life of hurt
			constructing = true;
			staticCharIdx = 0;
		}

		// Don't want our index to overflow
		if (staticCharIdx >= encodingString.length()) 
		{
			throw new IllegalArgumentException("encodingString ended prematurely");
		}

		// Update our current character for the leaf
		char currentChar = encodingString.charAt(staticCharIdx++);
		this.payloadChar = currentChar;

		// internal node
		if (currentChar == '^') // looks so sad :(
		{ 

			this.left = new MsgTree(encodingString); // Left child
			this.right = new MsgTree(encodingString); // Right child

		} 
		else 
		{ // if any other character, we create a leaf
			MsgTree leaf = new MsgTree(currentChar);
			this.payloadChar = leaf.payloadChar;
			this.left = leaf.left;
			this.right = leaf.right;
		}

		if (isRoot)
		{
			constructing = false;
		}
	}

	/**
	 * build a binary tree using the iterative approach. Let's take on the challenge
	 * :*)
	 * 
	 * @param encodingString
	 * @return
	 */
	public static MsgTree MsgTreeIterative(String encodingString) 
	{
		// A frame class so we can track each internal nodes/leaves and how many
		// children we've attached
		class Frame 
		{

			MsgTree node;
			int childCount;

			Frame(MsgTree n) 
			{
				node = n;
				childCount = 0;
			}
		}
		// Yeah we're checking this new tool out B)!
		Deque<Frame> stack = new ArrayDeque<>();

		// learned my lesson earlier.
		MsgTree root = null;

		// Grab our node characters
		for (int i = 0; i < encodingString.length(); i++) 
		{
			char c = encodingString.charAt(i);
			// Create a new node
			MsgTree node = new MsgTree(c);
			// so if we don't have a starting node
			if (root == null) 
			{

				// First character's always gonna be a node
				root = node;

				// yep that's a node
				if (c == '^') 
				{
					stack.push(new Frame(node));
				}
			}
			else // so now that we've moved past the first node
			{

				// Attach this node as a child of the top frame
				Frame top = stack.peek();

				// and just in case
				if (top == null) 
				{
					throw new IllegalStateException("Too many leaves or your encoding is malformed.");
				}

				// First child always is on the left
				if (top.childCount == 0) 
				{
					top.node.left = node;
					top.childCount++;
				}
				// Second child goes to the right
				else
				{
					top.node.right = node;
					stack.pop(); // We can only fit 2 children per each node.
				}

				// If the new node is internal, push it so we can attach *its* kids later
				if (c == '^') 
				{
					stack.push(new Frame(node));
				}
			}
		}

		if (!stack.isEmpty()) 
		{
			throw new IllegalArgumentException("Encoding string ended prematurely");
		}
		// and finally...
		return root;
	}
	
	/**
	 * Builds a node with null children
	 * @param payloadChar
	 */
	public MsgTree(char payloadChar) 
	{
		this.payloadChar = payloadChar;
		this.left = null;
		this.right = null;
	}
	
	/**
	 * Crucial helper method to build an encodingString that will be passed in MsgTree(String encodedString) for building our tree
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getEncodingString(String fileName) throws FileNotFoundException 
	{
		try (Scanner scnr = new Scanner(new File(fileName))) {
			
			//First line is always tree data (at least for our testing)
			if (!scnr.hasNextLine())
				throw new IllegalArgumentException("Empty file!");

			String part1 = scnr.nextLine(); //Doing this in 2 parts now

			//Read second line after we passed the error check
			if (!scnr.hasNextLine()) {
				throw new IllegalArgumentException("No binary line after tree!");
			}
			
			//Checks if the next line in file is encodingText or Binary
			String part2 = scnr.nextLine();

			//If part2 is purely binary then the tree was just one line (part1)
			if (part2.trim().matches("^[01]+$")) {
				return part1;
			}
			
			//Now we can actually generate a proper text body
			return part1 + '\n' + part2;
		}
	}
	
	/**
	 * Grab the binary section of the file as a text for later decoding
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getBinaryMessage(String fileName) throws FileNotFoundException 
	{
		try (Scanner scanner = new Scanner(new File(fileName))) 
		{
			StringBuilder binaryMessage = new StringBuilder();
			boolean hasBinary = false;

			while (scanner.hasNextLine()) 
			{
				String line = scanner.nextLine().trim();
				
				//Check if we've reached binary
				if (!hasBinary) 
				{
					//Check if this line consists only of 0s and 1s
					if (line.matches("^[01]+$")) 
					{
						hasBinary = true;
						binaryMessage.append(line);
					}
					//Else, we're still on encoding string
				} 
				else 
				{
					//Assumes we're already on binary previously, so we just append the rest
					binaryMessage.append(line);
				}
			}
			//Replaces any whitespace, "contracting" the line so it's all together
			return binaryMessage.toString().replaceAll("\\s", "");
		}
	}
	
	/**
	 * Decode() method unscramble's the code and reveals our message
	 * @param codes
	 * @param msg
	 */
	public void decode(MsgTree codes, String msg) 
	{
		// always gotta make sure we're catching this stuff
		if (msg.isEmpty()) 
		{
			System.out.println("Decoded message is empty.");
			return;
		}

		// Error if characters are other than 0's or 1's
		if (!msg.matches("[01]+")) 
		{
			System.out.println("Error: Invalid characters found in the binary message.");
			return;
		}

		MsgTree current = codes;
		StringBuilder decodedMessage = new StringBuilder();

		// Navigate through our binary message
		for (int i = 0; i < msg.length(); i++)
		{
			if (msg.charAt(i) == '0') 
			{
				current = current.left;
			} 
			else if (msg.charAt(i) == '1') 
			{
				current = current.right;
			}

			// We reached a leaf node, so we'll append the current character and reset to
			// the root
			if (current.left == null && current.right == null)
			{
				decodedMessage.append(current.payloadChar);
				current = codes;
			}
		}
		System.out.println(decodedMessage.toString());
	}
	
	/**
	 * Print characters and associating binary codes recursively
	 * in the following format:
	 * 
	 * character  code
	 * -------------------------
	 *   c        1011
	 *   r        110
	 *   b		  111
	 *   
	 * MESSAGE:
	 * The quick brown fox jumped over the lazy dog
	 * @param root
	 * @param code
	 */
	public static void printCodes(MsgTree root, String code) 
	{
		if (root == null)
			return;

		if (root.left == null && root.right == null)
		{
			char c = root.payloadChar;
			String displayChar;

			// Really "switching" things up with my formatting
			switch (c) 
			{
			case ' ':
				displayChar = "' '"; //Space
				break;
			case '\n':
				displayChar = "'\\n'"; //Newline
				break;
			case '\t':
				displayChar = "'\\t'"; //Tab
				break;
			default:
				displayChar = Character.toString(c);
			}
			System.out.printf("  %-7s %s\n", displayChar, code);
			return;
		}
		// Might as well do more recursion :*)
		printCodes(root.left, code + "0"); // Left edge = 0
		printCodes(root.right, code + "1"); // Right edge = 1
	}
	
	/**
	 * Grab and calculate the data statistics for us data nerds
	 * 
	 * @param tree
	 * @param binaryMessage
	 */
	public static void calculateAndPrintStatistics(MsgTree tree, String binaryMessage) 
	{
		int totalChars = 0;
		int totalBits = binaryMessage.length();

		// Decode message again and count characters
		MsgTree current = tree;
		for (int i = 0; i < binaryMessage.length(); i++) 
		{
			// Navigation!!!
			if (binaryMessage.charAt(i) == '0') 
			{
				current = current.left;
			} 
			else
			{
				current = current.right;
			}

			// When we reach a leaf node
			if (current.left == null && current.right == null) 
			{
				totalChars++;
				// Reset to root
				current = tree;
			}
		}   
        // Calculate statistics
        double avgBitsPerChar = (double) totalBits / totalChars;
        double spaceSavings = (1.0 - ((double) totalBits / (totalChars * 16.0))) * 100.0;
      
        // Print statistics based on .pdf formatting
        System.out.println("\nSTATISTICS:");
        System.out.printf("Avg bits/char: %.1f\n", avgBitsPerChar);
        System.out.println("Total characters: " + totalChars);
        System.out.printf("Space savings: %.1f%%\n", spaceSavings);
    }
}