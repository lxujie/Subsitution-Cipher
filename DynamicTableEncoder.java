import java.util.HashMap;
import java.util.Map;

/**
 * Interface implementation.
 */
interface SubstitutionCipher {
    String encode(String plainText);
    String decode(String encodedText);
}

/**
 * Implementation of the dynamic shift table encoder.
 */
public class DynamicTableEncoder implements SubstitutionCipher {

    // The ordered reference sequence containing exactly 44 characters based on the specification
    private static final String REFERENCE_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789()*+,-./";
    private static final int TABLE_SIZE = REFERENCE_STRING.length();

    // Bidirectional maps for O(1) index lookups and character retrievals
    private final Map<Character, Integer> charToIndexMap = new HashMap<>();
    private final Map<Integer, Character> indexToCharMap = new HashMap<>();
    
    // Configurable offset character for this instance
    private final char offsetCharacter;
    private final int shiftValue;

    /**
     * Constructor allowing injection of any valid offset character.
     * @param offsetCharacter The character to determine the table shift.
     */
    public DynamicTableEncoder(char offsetCharacter) {
        // Initialize mapping tables
        for (int i = 0; i < REFERENCE_STRING.length(); i++) {
            char c = REFERENCE_STRING.charAt(i);
            charToIndexMap.put(c, i);
            indexToCharMap.put(i, c);
        }

        // Validate and assign offset configuration
        if (!charToIndexMap.containsKey(offsetCharacter)) {
            throw new IllegalArgumentException("Offset character must exist within the reference table.");
        }
        this.offsetCharacter = offsetCharacter;
        this.shiftValue = charToIndexMap.get(offsetCharacter);
    }

    /**
     * Encodes plaintext into an obfuscated string prefixed with the offset character.
     */
    @Override
    public String encode(String plainText) {
        if (plainText == null) return null;

        StringBuilder encodedResult = new StringBuilder();
        // Rule: The first character of the encoded message must be the offset character
        encodedResult.append(offsetCharacter);

        for (int i = 0; i < plainText.length(); i++) {
            char currentChar = plainText.charAt(i);

            if (charToIndexMap.containsKey(currentChar)) {
                int originalIndex = charToIndexMap.get(currentChar);
                // Shift down: Subtract shiftValue and handle negative wrapping smoothly
                int newIndex = (originalIndex - shiftValue) % TABLE_SIZE;
                if (newIndex < 0) {
                    newIndex += TABLE_SIZE;
                }
                encodedResult.append(indexToCharMap.get(newIndex));
            } else {
                // Rule: Any character not in the reference table maps back to itself
                encodedResult.append(currentChar);
            }
        }

        return encodedResult.toString();
    }

    /**
     * Decodes an string by extracting the prefix offset character to reverse the shift.
     */
    @Override
    public String decode(String encodedText) {
        if (encodedText == null || encodedText.isEmpty()) return encodedText;

        // Rule: Extract the first character to discover the offset context
        char dynamicOffsetChar = encodedText.charAt(0);
        
        if (!charToIndexMap.containsKey(dynamicOffsetChar)) {
            throw new IllegalArgumentException("Encoded string contains an invalid prefix offset character.");
        }
        
        int dynamicShiftValue = charToIndexMap.get(dynamicOffsetChar);
        StringBuilder decodedResult = new StringBuilder();

        // Process the remainder of the text starting from index 1
        for (int i = 1; i < encodedText.length(); i++) {
            char currentChar = encodedText.charAt(i);

            if (charToIndexMap.containsKey(currentChar)) {
                int encodedIndex = charToIndexMap.get(currentChar);
                // Shift up: Add the shift value back to locate original character index
                int originalIndex = (encodedIndex + dynamicShiftValue) % TABLE_SIZE;
                decodedResult.append(indexToCharMap.get(originalIndex));
            } else {
                // Unmapped characters remain identical
                decodedResult.append(currentChar);
            }
        }

        return decodedResult.toString();
    }

    // Quick verification execution matching the assignment prompt examples
    public static void main(String[] args) {
        String testInput = "HELLO WORLD";

        // Test Scenario 1: Using 'B' as the offset
        SubstitutionCipher encoderB = new DynamicTableEncoder('B');
        String encryptedB = encoderB.encode(testInput);
        System.out.println("Offset 'B' Encoded: " + encryptedB); // Target output: BGDKKN VNQKC
        System.out.println("Offset 'B' Decoded: " + encoderB.decode(encryptedB));

        System.out.println("---");

        // Test Scenario 2: Using 'F' as the offset
        SubstitutionCipher encoderF = new DynamicTableEncoder('F');
        String encryptedF = encoderF.encode(testInput);
        System.out.println("Offset 'F' Encoded: " + encryptedF); 
        System.out.println("Offset 'F' Decoded: " + encoderF.decode(encryptedF));
    }
}