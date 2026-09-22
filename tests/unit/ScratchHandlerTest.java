package lab21;

import java.math.BigInteger;
import java.util.Random;

/** Executable contract checks for the Lab2.1 draft; no external test dependency is required. */
public final class ScratchHandlerTest {
  private static final ScratchHandler HANDLER = new ScratchHandler();
  private static int checks;

  private ScratchHandlerTest() {}

  /**
   * Runs contract and independent reference checks, failing the process on any mismatch.
   *
   * @param args unused command-line arguments
   */
  public static void main(String[] args) {
    testValidInputs();
    testInvalidInputs();
    testAgainstBigInteger();
    System.out.println("PASS: " + checks + " checks (contract, validation, BigInteger reference).");
  }

  private static void testValidInputs() {
    assertSum("ordinary addition", "12", "30", "42");
    assertSum("single digit carry", "9", "9", "18");
    assertSum("carry chain", "999", "1", "1000");
    assertSum("longer second operand", "1", "999", "1000");
    assertSum("internal carry", "909", "101", "1010");
    assertSum("zero", "0", "0", "0");
    assertSum("all leading zeros", "000", "0000", "0");
    assertSum("leading zeros", "00012", "00008", "20");
    assertSum("identity", "123456789", "0", "123456789");
    assertSum("beyond long", "9223372036854775807", "1", "9223372036854775808");
    assertSum("very large integer", "9".repeat(100), "1", "1" + "0".repeat(100));
    assertSum("maximum length", "9".repeat(10_000), "1", "1" + "0".repeat(10_000));
    assertSum("both maximum length", "9".repeat(10_000), "9".repeat(10_000),
        "1" + "9".repeat(9_999) + "8");
    assertSum("maximum zero padding", "0".repeat(10_000), "0", "0");
  }

  private static void testInvalidInputs() {
    String[] invalidInputs = {
      null, "", " ", " 12", "12 ", "1 2", "-1", "+1", "1.5", "1e3", "12a",
      "1\n2", "12\n", "\t12", "\u0661\u0662", "\uff11\uff12", "1\u00002",
      "9".repeat(10_001), "sensitive-input@example.invalid"
    };
    int index = 0;
    while (index < invalidInputs.length) {
      assertInvalid("invalid first operand " + index, invalidInputs[index], "1", "first");
      assertInvalid("invalid second operand " + index, "1", invalidInputs[index], "second");
      index++;
    }
  }

  private static void testAgainstBigInteger() {
    Random random = new Random(21L);
    int index = 0;
    String first;
    String second;
    String expected;
    while (index < 250) {
      first = "00" + new BigInteger(1 + random.nextInt(4096), random).toString();
      second = "000" + new BigInteger(1 + random.nextInt(4096), random).toString();
      expected = new BigInteger(first).add(new BigInteger(second)).toString();
      assertSum("reference case " + index, first, second, expected);
      assertSum("commutative case " + index, second, first, expected);
      index++;
    }
  }

  private static void assertSum(String label, String first, String second, String expected) {
    if (!expected.equals(HANDLER.sum(first, second))) {
      throw new AssertionError(label + ": unexpected sum");
    }
    checks++;
  }

  private static void assertInvalid(String label, String first, String second, String operandName) {
    try {
      HANDLER.sum(first, second);
    } catch (IllegalArgumentException exception) {
      String message = exception.getMessage();
      if (!(operandName + " is required").equals(message)
          && !(operandName + " must contain at most 10000 digits").equals(message)
          && !(operandName + " must contain only ASCII digits").equals(message)) {
        throw new AssertionError(label + ": unexpected or unsafe error message");
      }
      if (exception.getCause() != null) {
        throw new AssertionError(label + ": input must not be exposed through a cause");
      }
      checks++;
      return;
    }
    throw new AssertionError(label + ": invalid input was accepted");
  }
}
