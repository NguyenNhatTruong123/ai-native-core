package lab21;

/** Draft for Lab2.1: adds non-negative integers represented by ASCII digit strings. */
public final class ScratchHandler {
  private static final int MAX_DIGITS = 10_000;

  /**
   * Adds two non-negative integers without converting the operands to primitive numeric types.
   *
   * <p>Each operand must contain 1 to 10000 ASCII digits. Leading zeros are allowed. This method
   * takes O(n) time and O(n) space, where n is the length of the longer operand.
   *
   * @param first the first non-negative integer as an ASCII digit string
   * @param second the second non-negative integer as an ASCII digit string
   * @return the sum without leading zeros, or {@code "0"} if the sum is zero
   * @throws IllegalArgumentException if either operand is null, empty, too long, or contains a
   *     character outside ASCII {@code '0'} to {@code '9'}
   */
  public String sum(String first, String second) {
    validateNumber(first, "first");
    validateNumber(second, "second");

    StringBuilder result = new StringBuilder(Math.max(first.length(), second.length()) + 1);
    int firstIndex = first.length() - 1;
    int secondIndex = second.length() - 1;
    int carry = 0;
    int digitSum;

    while (firstIndex >= 0 || secondIndex >= 0 || carry != 0) {
      digitSum = carry;
      if (firstIndex >= 0) {
        digitSum += first.charAt(firstIndex) - '0';
        firstIndex--;
      }
      if (secondIndex >= 0) {
        digitSum += second.charAt(secondIndex) - '0';
        secondIndex--;
      }
      result.append(digitSum % 10);
      carry = digitSum / 10;
    }

    result.reverse();
    int firstNonZero = 0;
    while (firstNonZero < result.length() - 1 && result.charAt(firstNonZero) == '0') {
      firstNonZero++;
    }
    return result.substring(firstNonZero);
  }

  private static void validateNumber(String value, String operandName) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException(operandName + " is required");
    }
    if (value.length() > MAX_DIGITS) {
      throw new IllegalArgumentException(operandName + " must contain at most 10000 digits");
    }

    int index = 0;
    char digit;
    while (index < value.length()) {
      digit = value.charAt(index);
      if (digit < '0' || digit > '9') {
        throw new IllegalArgumentException(operandName + " must contain only ASCII digits");
      }
      index++;
    }
  }
}
