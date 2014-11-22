package de.uniwue.info6.misc;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import de.uniwue.info6.database.map.User;

/**
 *
 *
 * @author Michael
 */
public class StringTools {

	private static SecureRandom random = new SecureRandom();

	/**
	 *
	 *
	 * @return
	 */
	public static String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}

	/**
	 *
	 *
	 * @return
	 */
	public static String generatePassword(int numBits, int length) {
		return new BigInteger(numBits, random).toString(length);
	}

	/**
	 *
	 *
	 * @param par
	 * @return
	 */
	public static boolean parseBoolean(String par) {
		if (par.trim().equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static String stripHtmlTags(String text) {
		if (text != null) {
			// strip html tags
			text = text.replaceAll("<[^>]*>", "");
		}
		return text;
	}

	/**
	 *
	 *
	 * @param text
	 * @param leftIndex
	 * @return
	 */
	public static boolean trailingCharacter(String text, int index, boolean left) {
		boolean hasValidTrailingCharacter = false;

		if ((left && index == 0) || (!left && index == text.length()))
			return true;

		while ((left && index != 0) || (!left && index != text.length())) {
			Character c = left ? text.charAt(--index) : text.charAt(index++);
			if (!Character.isWhitespace(c)) {
				if (c == '.')
					return left ? false : true;
				if (c == ';' || c == ',' || c == '`' || c == ')' || c == '(')
					hasValidTrailingCharacter = true;
				index = left ? 0 : text.length();
			} else {
				hasValidTrailingCharacter = true;
			}
		}

		if (hasValidTrailingCharacter)
			return true;
		return false;
	}

	/**
	 *
	 *
	 * @param queryText
	 * @param tables
	 * @param user
	 * @return
	 */
	public static String addUserPrefix(String queryText, List<String> tables, User user) {
		LinkedList<Integer> substrings = new LinkedList<Integer>();
		int start = 0, end = queryText.length();
		for (String tab : tables) { // q&d fix
			Matcher matcher = Pattern.compile(tab.trim(), Pattern.CASE_INSENSITIVE).matcher(queryText);
			while (matcher.find()) {
				start = matcher.start(0);
				end = matcher.end(0);
				// String group = matcher.group();
				boolean leftCharacterValid = StringTools.trailingCharacter(queryText, start, true);
				boolean rightCharacterValid = StringTools.trailingCharacter(queryText, end, false);
				if (leftCharacterValid && rightCharacterValid) {
					substrings.add(start);
				}
			}
		}

		Collections.sort(substrings);

		for (int i = substrings.size() - 1; i >= 0; i--) {
			Integer sub = substrings.get(i);
			queryText = queryText.substring(0, sub) + user.getId() + "_" + queryText.substring(sub, queryText.length());
		}

		return queryText;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static String stripHtmlTagsForQuestion(String text) {
		if (text != null) {
			text = text.replaceAll("(?i)<script.*?</script>", "");
			text = text.replaceAll("(?i)<javascript.*?</javascript>", "");
			text = text.replaceAll("(?i)<style.*?</style>", "");

			String REGEX_FIELD = "<[^>]*>";
			Matcher matcher = Pattern.compile(REGEX_FIELD, Pattern.CASE_INSENSITIVE).matcher(text);
			while (matcher.find()) {
				String snippet = matcher.group();
				String snippetNoSpaces = snippet.replaceAll("[\\s\"\'0-9]", "");

				if (!snippetNoSpaces.equals("<br>") && !snippetNoSpaces.equals("<br/>")
						&& !snippetNoSpaces.equals("</span>") && !snippetNoSpaces.equals("<span>")) {

					if (snippetNoSpaces.startsWith("<span")) {
						if (!snippetNoSpaces.equals("<spanstyle=font-style:italic;>")
								&& !snippetNoSpaces.equals("<spanstyle=text-decoration:underline;>")
								&& !snippetNoSpaces.equals("<spanstyle=text-decoration:line-through;>")
								&& !snippetNoSpaces.equals("<spanstyle=color:rgb(,,);>")) {
							text = text.replace(snippet, "<span>");
						}
					} else {
						text = text.replaceFirst(snippet, "");
					}
				}
			}
		}
		return text;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static String stripHtmlTagsForScenario(String text) {
		if (text != null) {
			text = text.replaceAll("(?i)<script.*?</script>", "");
			text = text.replaceAll("(?i)<javascript.*?</javascript>", "");
			text = text.replaceAll("(?i)<style.*?</style>", "");

			String REGEX_FIELD = "<[^>]*>";
			Matcher matcher = Pattern.compile(REGEX_FIELD, Pattern.CASE_INSENSITIVE).matcher(text);
			while (matcher.find()) {
				String snippet = matcher.group();
				String snippetNoSpaces = snippet.toLowerCase().replaceAll("[\\s\"\'0-9]", "");

				if (!snippetNoSpaces.equals("<br>") && !snippetNoSpaces.equals("<br/>")
						&& !snippetNoSpaces.equals("</span>") && !snippetNoSpaces.equals("<span>")
						&& !snippetNoSpaces.equals("</font>") && !snippetNoSpaces.equals("<font>")) {

					if (snippetNoSpaces.startsWith("<span")) {
						if (!snippetNoSpaces.equals("<spanstyle=font-style:italic;>")
								&& !snippetNoSpaces.equals("<spanstyle=text-decoration:underline;>")
								&& !snippetNoSpaces.equals("<spanstyle=text-decoration:line-through;>")
								&& !snippetNoSpaces.equals("<spanstyle=color:rgb(,,);>")
								&& !snippetNoSpaces.equals("<spanstyle=font-weight:bold;>")) {
							text = text.replace(snippet, "<span>");
						}
					} else if (snippetNoSpaces.startsWith("<font")) {
						if (!snippetNoSpaces.equals("<fontsize=>")) {
							text = text.replace(snippet, "<font>");
						}
					} else {
						text = text.replaceFirst(snippet, "");
					}
				}
			}
		}
		return text;
	}

	/**
	 *
	 *
	 * @param path
	 * @return
	 */
	public static String normalizeFileName(String path) {
		String newName = FilenameUtils.removeExtension(path).toLowerCase();
		return normalize(newName);
	}

	/**
	 *
	 *
	 * @param path
	 * @return
	 */
	public static String deleteDate(String path) {
		if (path != null) {
			return path.replaceAll("[0-9]{4}_[0-9]{2}_[0-9]{2}-[0-9]{2}_[0-9]{2}_[0-9]{2}", "");
		}
		return null;
	}

	/**
	 *
	 *
	 * @param string
	 * @param length
	 * @return
	 */
	public static String trimToLengthIndicator(String s, int length) {
		if (length < 5) {
			return "[...]";
		}
		if (s.length() > 5 && s.length() > length - 5) {
			return s.substring(0, length - 5) + "[...]";
		} else {
			return s;
		}
	}

	/**
	 *
	 *
	 * @param s
	 * @param length
	 * @return
	 */
	public static String trimToLength(String s, int length) {
		if (s.length() > length) {
			return s.substring(0, length);
		} else {
			return s;
		}
	}

	/**
	 *
	 *
	 * @param stringToSearch
	 * @param length
	 * @return
	 */
	public static String findSnippet(String stringToSearch, String stringToFind, int length) {
		try {
			stringToFind = stringToFind.trim();
			stringToSearch = stringToSearch.trim();
			if (!stringToSearch.toLowerCase().contains(stringToFind.toLowerCase())) {
				return trimToLengthIndicator(stringToSearch, length);
			}

			int mainLength = stringToSearch.length();
			int snippetLength = stringToFind.length();
			String end = "[...]";

			if (length > 5 && snippetLength < length) {
				int index = stringToSearch.toLowerCase().indexOf(stringToFind.toLowerCase());

				if (snippetLength < length && snippetLength + 10 > length) {
					if (snippetLength >= length - 5) {
						return stringToFind.substring(0, length - 5) + end;
					} else {
						return stringToFind + end;
					}
				}

				boolean leftTrim = false, rightTrim = false;
				boolean trimRight = false;
				int spaceLeft = 0, spaceRight = 0;

				spaceLeft = index;
				spaceRight = mainLength - (spaceLeft + snippetLength);

				while ((mainLength > length - 10) || (mainLength > length - 5 && index == 0)) {
					if (trimRight || index == 0) {
						if (spaceRight > 0) {
							stringToSearch = stringToSearch.substring(0, stringToSearch.length() - 1);
							rightTrim = true;
						}
						if (spaceLeft > spaceRight || spaceRight == 0) {
							trimRight = false;
						}
					} else {
						if (spaceLeft > 0) {
							stringToSearch = stringToSearch.substring(1, stringToSearch.length());
							leftTrim = true;
						}
						if (spaceLeft <= spaceRight || spaceLeft == 0) {
							trimRight = true;
						}
					}

					mainLength = stringToSearch.length();
					spaceLeft = stringToSearch.toLowerCase().indexOf(stringToFind.toLowerCase());
					spaceRight = mainLength - (spaceLeft + snippetLength);
				}

				if (rightTrim) {
					stringToSearch += end;
				}
				if (leftTrim) {
					stringToSearch = end + stringToSearch;
				}

				return stringToSearch;
			} else {
				return trimToLengthIndicator(stringToSearch, length);
			}
		} catch (Exception e) {
			return stringToSearch;
		}
	}

	/**
	 *
	 *
	 * @param query
	 * @return
	 */
	public static String extractIDFromAutoComplete(String query) {
		if (query != null) {
			String[] temp = query.split("]:");
			if (temp.length == 2) {
				return temp[0].replace("[", "").trim();
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param path
	 * @return
	 */
	public static String normalize(String path) {
		if (path != null) {
			try {
				String newName = path.toLowerCase();
				// delete date-field
				newName = newName.replaceAll("[ü}]", "ue").replaceAll("[ä]", "ae").replaceAll("[ö]", "oe")
						.replaceAll("[ß]", "ss").replaceAll("[^a-z0-9&-]", "_").replaceAll("[_]{2,}", "_");

				while (newName.endsWith("_")) {
					newName = newName.substring(0, newName.length() - 1);
				}
				while (newName.startsWith("_")) {
					newName = newName.substring(1, newName.length());
				}
				return newName;
			} catch (Exception e) {

			}
		}
		return path;
	}

	/**
	 *
	 *
	 * @param number
	 * @param width
	 * @return
	 */
	public static String zeroPad(int number, int width) {
		if (number < 100) {
			int wrapAt = (int) Math.pow(10, width);
			return String.valueOf(number % wrapAt + wrapAt).substring(1);
		} else {
			return String.valueOf(number);
		}
	}
}
