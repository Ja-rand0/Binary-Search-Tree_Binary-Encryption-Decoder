package BinarySearchTrees;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Christian Salazar
 */
public class BinaryMain {

	public static void main(String[] args) 
	{

		Scanner scnr = new Scanner(System.in);

		try 
		{
			while (true) 
			{
				String fileName = null;
				boolean validFile = false;

				while (!validFile)
				{
					System.out.println("Enter the filename: ");
					fileName = scnr.nextLine();

					try 
					{
						String encodingString = MsgTree.getEncodingString(fileName);
						String binaryMessage = MsgTree.getBinaryMessage(fileName);

						validFile = true;

						//Which approach do you want to test out? If invalid input, we're gonna default to recursive.
						System.out.println("Type either number below for method of tree construction: ");
						System.out.println("(1) | Recursive Method");
						System.out.println("(2) | Iterative Method");

						String methodChoice = scnr.nextLine();
						MsgTree tree;

						if (methodChoice.equals("1")) 
						{
							// Recursive Method
							tree = new MsgTree(encodingString);
						}
						else if (methodChoice.equals("2")) 
						{
							// Iterative Method
							tree = MsgTree.MsgTreeIterative(encodingString);
						} 
						else 
						{
							System.out.println("Invalid choice so we're gonna default to recursive >:)");
							tree = new MsgTree(encodingString); // Default to recursive
						}

						System.out.println("character code");
						System.out.println("-------------------------");
						MsgTree.printCodes(tree, "");

						System.out.println("\nMESSAGE:");

						// Decode and print the message
						tree.decode(tree, binaryMessage);

						MsgTree.calculateAndPrintStatistics(tree, binaryMessage);

					} catch (FileNotFoundException e) {
						System.err.println("File not found. Please try again");
					}
				}

				System.out.println("Type: 'y' to continue | 'n' to stop ");
				String choice = scnr.nextLine();

				if (choice.equalsIgnoreCase("n")) {
					System.out.println("Goodbye ˙◠˙");
					break;
				} else if (choice.equalsIgnoreCase("y")) {
					System.out.println("Woohoo! ৻( •̀ ᗜ •́  ৻)");
					continue;
				} else {
					System.out.println("I'll assume you meant 'y'... ಠ_ಠ");
				}
			}

		} finally {
			scnr.close();
		}
	}
}
