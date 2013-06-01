package binnie.apps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Binnie
 * Date: 24/03/13
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class NumberDrum extends Activity {
    private int[] nums = new int[6];
    private int targetNum;
    private String[] currentAnswer = new String[30];
    private int currentAnswerLength;
    private int differenceInBrackets = 0;
    public static final int OPERATOR = 1, NUMBER = 2, LEFT_BRACKET = 3, RIGHT_BRACKET = 4;
    public static final int MULTIPLY = 3, DIVIDE = 4, ADD = 1, SUBTRACT = 2;
    private int lastChar = OPERATOR;
    private int numbersUsed = 0;
    private int numHintsUsed = 0;
    private Button[] numberButtons = new Button[6];
    private List<Integer> buttonsOrder = new ArrayList<Integer>(6);
    private Button targetButton;
    private TextView equationTextView;
    private String[] equation = new String[30];
    private int equationIndex = 0;
    private Chronometer timer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberdrum_game);

        Intent intent = getIntent();
        if (intent.getBooleanExtra(MainMenu.IS_NEW_GAME, true)) {
            generateNewGame();
        }
        setUpGame();
    }

    public void setUpGame() {

        numberButtons[0] = (Button) findViewById(R.id.num1);
        numberButtons[1] = (Button) findViewById(R.id.num2);
        numberButtons[2] = (Button) findViewById(R.id.num3);
        numberButtons[3] = (Button) findViewById(R.id.num4);
        numberButtons[4] = (Button) findViewById(R.id.num5);
        numberButtons[5] = (Button) findViewById(R.id.num6);

        buttonsOrder.clear();
        for (int ii = 0; ii < 6; ii++) buttonsOrder.add(ii);
        Collections.shuffle(buttonsOrder);

        for (int ii = 0; ii < 6; ii++) {
            numberButtons[buttonsOrder.get(ii)].setText(Integer.toString(nums[ii]));
            numberButtons[ii].setWidth(60);
        }
        targetButton = (Button) findViewById(R.id.target_num);
        targetButton.setText(Integer.toString(targetNum));
        targetButton.setWidth(90);
        targetButton.setClickable(false);

        equationTextView = (TextView) findViewById(R.id.equation);
        equationTextView.setText("");

        timer = (Chronometer) findViewById(R.id.timer);
        timer.start();
    }

    public void numberPressed(View view) {
        if ((lastChar == OPERATOR) || (lastChar == LEFT_BRACKET)) {
            TextView textView = (TextView)view;
            equation[equationIndex] = textView.getText().toString();
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = NUMBER;
            numbersUsed++;

            Button button = (Button) findViewById(view.getId());
            button.setEnabled(false);

            if ((numbersUsed == 6) && (differenceInBrackets == 0)) setSubmitButton();
        }
    }

    public void operatorPressed (View view) {
        if ((lastChar == NUMBER) || (lastChar == RIGHT_BRACKET)) {
            TextView textView = (TextView)view;
            equation[equationIndex] = textView.getText().toString();
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = OPERATOR;
        }
    }

    public void leftBracketPressed (View view) {
        if (((lastChar == OPERATOR) || (lastChar == LEFT_BRACKET)) &&
            (differenceInBrackets <= (6-(numbersUsed + 2)))) {
            differenceInBrackets++;
            equation[equationIndex] = "(";
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = LEFT_BRACKET;
        }
    }

    public void rightBracketPressed (View view) {
        if (((lastChar == NUMBER) || (lastChar == RIGHT_BRACKET)) &&
            (differenceInBrackets > 0)) {
            differenceInBrackets--;
            equation[equationIndex] = ")";
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = RIGHT_BRACKET;

            if ((numbersUsed == 6) && (differenceInBrackets == 0)) setSubmitButton();
        }
    }

    public void submitAnswer (View view) {
        // Calculate the answer, and then if it's correct, show a successful dialog / activity.
        // Otherwise just show a message dialog, and then go back to the same screen.
        if ((evalEquation(equation, equationIndex)) == targetNum) {
            targetButton.setText("Success");
            StatsStore.updateStats(true, SystemClock.elapsedRealtime() - timer.getBase());
        }
        else {
            targetButton.setText(Integer.toString(targetNum));
            StatsStore.updateStats(false, SystemClock.elapsedRealtime() - timer.getBase());
            resetGame(findViewById(R.id.reset));
        }
    }

    private void setSubmitButton () {
        targetButton.setClickable(true);
        targetButton.setText("Submit Answer");
    }

    public void startNewGame (View view) {
        // Should raise a dialog here.  The dialog should say that the previous game will be counted negatively
        // towards any stats that are being counted.
        // Don't need this function at the moment to get a prototype going.
        resetGame(findViewById(R.id.reset));
        generateNewGame();
        numHintsUsed = 0;
        timer.stop();
        timer.setBase(SystemClock.elapsedRealtime());
        setUpGame();
    }

    public void resetGame (View view) {
        equationTextView.setText("");
        Button hintButton = (Button) findViewById(R.id.hint);
        hintButton.setText("Hint");
        for (int ii = 0; ii < equation.length; ii++) equation[ii] = "";
        equationIndex = 0;
        numbersUsed = 0;
        differenceInBrackets = 0;
        lastChar = OPERATOR;
        for (Button button: numberButtons) button.setEnabled(true);
        targetButton.setClickable(false);
        targetButton.setText(Integer.toString(targetNum));
    }

    public void giveHint (View view) {
        resetGame(findViewById(R.id.reset));
        if (numHintsUsed < 4) {
            timer.setBase(timer.getBase() - 30000 * (long)Math.pow(2, numHintsUsed));
            numHintsUsed++;
        }
        int numsRemainingToGive = numHintsUsed + 1;
        equationIndex = 0;
        differenceInBrackets = 0;
        numbersUsed = 0;
        while (numsRemainingToGive > 0) {
            equation[equationIndex] = currentAnswer[equationIndex];
            if (isInt(equation[equationIndex])) {
                numberButtons[buttonsOrder.get(numbersUsed)].setEnabled(false);
                numbersUsed++;
                numsRemainingToGive--;
            }
            if (equation[equationIndex].equals("(")) differenceInBrackets++;
            if (equation[equationIndex].equals(")")) differenceInBrackets--;
            equationIndex++;
        }

        while (currentAnswer[equationIndex].equals(")")) {
            equation[equationIndex] = currentAnswer[equationIndex];
            equationIndex++;
            differenceInBrackets--;
        }

        if (equation[equationIndex-1].equals(")")) lastChar = RIGHT_BRACKET;
        else lastChar = NUMBER;

        String textString = "";
        for (int ii = 0; ii < equationIndex; ii++) textString += equation[ii];
        equationTextView.setText(textString);

    }

    static boolean isInt(String s) {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }

    private void generateNewGame() {
        List<String> returnList = new ArrayList<String>();
        int[] ops = new int[5];
        int[] bracketPositions;
        Random random = new Random();
        int equationValue;
        do {
            returnList.clear();
            for (int ii = 0; ii < 6; ii++) {
                nums[ii] = random.nextInt(10)+1;
                returnList.add(Integer.toString(nums[ii]));
                if (ii < 5) {
                    int rand = random.nextInt(3);
                    returnList.add(getOpFromInt(rand));
                    ops[ii] = rand + 1;
                }
            }
            //for (int ii = 4; ii < 6; ii++) returnString[2*ii] = Integer.toString((random.nextInt(4)+1)*25);
            List<Integer> list = new ArrayList<Integer>();
            for (int ii = 0; ii < 5; ii++) list.add(ii);
            Collections.shuffle(list);

            bracketPositions = generateBracketPositions(ops, list);
            int currPos = 0;
            for (int ii = 0; ii < 6; ii++) {
                if (ii < 5) {
                    for (int jj = 0; jj < bracketPositions[ii]; jj++) {
                        returnList.add(currPos, "(");
                        currPos++;
                    }
                }
                currPos++;
                if (ii > 0) {
                    for (int jj = 0; jj < bracketPositions[ii + 4]; jj++) {
                        returnList.add(currPos, ")");
                        currPos++;
                    }
                }
                currPos++;
            }

            currentAnswerLength = returnList.size();
            for (int ii = 0; ii < currentAnswerLength; ii++) currentAnswer[ii] = returnList.get(ii);
            equationValue = evalEquation(currentAnswer, returnList.size());
        } while ((equationValue > 1000) || (equationValue <= 0));
        targetNum = equationValue;
    }

    private int[] generateBracketPositions(int[] ops, List<Integer> order) {
        int[] returnString = new int[]{0,0,0,0,0,0,0,0,0,0};
        boolean[] opUsed = new boolean[]{false, false, false, false, false};
        int opPosition;
        int currOp;
        int prevOp = 0;
        int nextOp = 0;
        boolean incBrackets;
        int startBracketPos;
        int endBracketPos;
        for (int ii = 0; ii < 5; ii++) {
            opPosition = order.get(ii);
            startBracketPos = opPosition;
            while ((startBracketPos > 0) && (opUsed[startBracketPos - 1])) startBracketPos--;
            endBracketPos = opPosition;
            while ((endBracketPos < 4) && (opUsed[endBracketPos + 1])) endBracketPos++;
            currOp = ops[opPosition];
            if (startBracketPos > 0) prevOp = ops[startBracketPos - 1];
            if (endBracketPos < 4) nextOp = ops[endBracketPos + 1];
            incBrackets = false;
            if ((startBracketPos != 0) || (endBracketPos != 4)) {
                switch (currOp) {
                    case ADD:
                        if (((startBracketPos != 0) && (prevOp != ADD)) ||
                                ((endBracketPos != 4) && (nextOp != ADD) && (nextOp != SUBTRACT))) incBrackets = true;
                        break;

                    case SUBTRACT:
                        if (((startBracketPos != 0) && (prevOp != ADD)) ||
                                ((endBracketPos != 4) && (nextOp != ADD) && (nextOp != SUBTRACT))) incBrackets = true;
                        break;

                    case MULTIPLY:
                        if ((startBracketPos != 0) && (prevOp == DIVIDE)) incBrackets = true;
                        break;

                    case DIVIDE:
                        if ((startBracketPos != 0) && ((prevOp == MULTIPLY) || (prevOp == DIVIDE))) incBrackets = true;
                        break;

                    default:
                        break;

                }
                if (incBrackets) {
                    returnString[startBracketPos]++;
                    returnString[endBracketPos + 5]++;
                }
            }
            opUsed[opPosition] = true;
        }
        return returnString;
    }

    private String getOpFromInt(int number) {
        if (number == 0) return "+";
    //    if (number == 1) return "/";
        if (number == 1) return "-";
        else return "*";
    }

    // If any intermediate expressions are not positive integers, then return -1
    // If any division is not soluble, then return -2
    // Else return the number given by the equationTextView
    // Should not be called on an equation with bad syntax, and should not be called on an empty string.
    private int evalEquation (String[] equationArray, int arrayLength) {
        int firstHalf;
        int secondHalf;
        int ii;
        int numRightMinusNumLeft = 0;
        int lastPlusOrMinus = arrayLength - 1;
        if (arrayLength == 1) return Integer.parseInt(equationArray[0]);

        while ((lastPlusOrMinus > 0) &&
                !((numRightMinusNumLeft == 0) && ((equationArray[lastPlusOrMinus].equals("+")) || (equationArray[lastPlusOrMinus].equals("-"))))) {
            if (equationArray[lastPlusOrMinus].equals("(")) numRightMinusNumLeft--;
            if (equationArray[lastPlusOrMinus].equals(")")) numRightMinusNumLeft++;
            lastPlusOrMinus--;
        }

        if (lastPlusOrMinus > 0) {
            firstHalf = evalEquation(Arrays.copyOfRange(equationArray, 0, lastPlusOrMinus), lastPlusOrMinus);
            String operator = equationArray[lastPlusOrMinus];
            secondHalf = evalEquation(Arrays.copyOfRange(equationArray, lastPlusOrMinus + 1, arrayLength), arrayLength - lastPlusOrMinus - 1);
            return evalEquationApplyOp(firstHalf, operator, secondHalf);
        }
        else if (equationArray[arrayLength - 1].equals(")")) {
            numRightMinusNumLeft = 1;
            ii = arrayLength - 1;
            while (numRightMinusNumLeft > 0) {
                ii--;
                if (equationArray[ii].equals("(")) numRightMinusNumLeft--;
                if (equationArray[ii].equals(")")) numRightMinusNumLeft++;
            }
            if (ii == 0) return evalEquation(Arrays.copyOfRange(equationArray, 1, arrayLength - 1), arrayLength - 2);
            else {
                firstHalf = evalEquation(Arrays.copyOfRange(equationArray, 0, ii-1), ii-1);
                String operator = equationArray[ii-1];
                secondHalf = evalEquation(Arrays.copyOfRange(equationArray, ii, arrayLength), arrayLength-ii);
                return evalEquationApplyOp(firstHalf, operator, secondHalf);
            }
        }
        else {
            firstHalf = evalEquation(Arrays.copyOfRange(equationArray, 0, arrayLength - 2), arrayLength - 2);
            String operator = equationArray[arrayLength - 2];
            secondHalf = Integer.parseInt(equationArray[arrayLength - 1]);
            return evalEquationApplyOp(firstHalf, operator, secondHalf);
        }
    }

    private int evalEquationApplyOp (int firstHalf, String operator, int secondHalf) {
        if ((firstHalf == -1) || (secondHalf == -1)) return -1;
        if ((firstHalf == -2) || (secondHalf == -2)) return -2;

        if (operator.equals("+")) return firstHalf + secondHalf;
        if (operator.equals("-")) {
            if ((firstHalf - secondHalf) <= 0) return -1;
            else return firstHalf - secondHalf;
        }
        if (operator.equals("*")) return firstHalf * secondHalf;
        if (operator.equals("/")) {
            if ((firstHalf % secondHalf) != 0) return -2;
            else return firstHalf / secondHalf;
        }
        assert (false);
        return 0;
    }
}